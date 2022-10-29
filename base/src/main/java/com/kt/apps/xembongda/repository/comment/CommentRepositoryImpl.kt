package com.kt.apps.xembongda.repository.comment

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.di.BaseScope
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import com.kt.apps.xembongda.storage.IKeyValueStorage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import javax.inject.Inject
import javax.inject.Singleton

@BaseScope
class CommentRepositoryImpl @Inject constructor(
    private val ketValueStorage: IKeyValueStorage
) : ICommentRepository {
    private val commentsDataBase by lazy {
        Firebase.firestore.collection("comments")
    }

    private val commentCountDataBase by lazy {
        val commentForUserId = Firebase.auth.currentUser?.uid ?: return@lazy null
        Firebase.database.getReference("comments")
            .child(commentForUserId)
    }
    private var commentNum: Int = 0
    private var commentSource: ObservableEmitter<Int>? = null

    override fun loadTotalCommentCount(): Observable<Int> {
        commentCountDataBase?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                this@CommentRepositoryImpl.commentNum = snapshot.getValue(Int::class.java) ?: 0
                commentSource?.onNext(commentNum)
                ketValueStorage.save("comments", commentNum, Int::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return Observable.create {
            commentSource = it
        }
    }

    private var currentMatchCommentId: String? = null

    override fun loadCommentFor(commentSpace: CommentSpace): Observable<List<CommentDTO>> {
        return Observable.create { emitter ->
            currentMatchCommentId = when (commentSpace) {
                is CommentSpace.Match -> commentSpace.footballMatch.getMatchIdForComment()
                else -> {
                    "total"
                }
            }
            getTotalComments(currentMatchCommentId!!, emitter)
        }
    }

    private fun getTotalCommentGlobal(emitter: ObservableEmitter<List<CommentDTO>>) {
        currentMatchCommentId = "total"
        getTotalComments(currentMatchCommentId!!, emitter)
    }

    private fun getTotalCommentForMatch(
        footballMatch: FootballMatch,
        emitter: ObservableEmitter<List<CommentDTO>>
    ) {
        val commentGroupId = footballMatch.getMatchIdForComment()
        currentMatchCommentId = commentGroupId
        getTotalComments(commentGroupId, emitter)
    }

    private fun getTotalComments(
        commentGroupId: String,
        emitter: ObservableEmitter<List<CommentDTO>>
    ) {
        val ref = commentsDataBase.document(commentGroupId)
        val task = ref.addSnapshotListener(MetadataChanges.EXCLUDE) { value, error ->
            value?.data?.values?.mapNotNull {
                CommentDTO.fromFireStoreStr(it.toString())
            }?.let { list ->
                emitter.onNext(list.sortedBy { it.systemTime }.reversed())
            }
        }
        emitter.setCancellable {
            task.remove()
        }
    }

    override fun sendCommentsFor(comment: CommentDTO, space: CommentSpace): Observable<CommentDTO> {
        if (commentNum <= 0) {
            return Observable.error(Throwable("Bạn đã vượt quá số lượt bình luận"))
        }
        return Observable.create { emitter ->
            when (space) {
                is CommentSpace.Match -> {
                    val match = space.footballMatch
                    commentToMatch(comment, match, emitter)
                }
                else -> {

                }
            }
        }
    }

    private fun commentToMatch(
        commentDTO: CommentDTO,
        match: FootballMatch,
        emitter: ObservableEmitter<CommentDTO>
    ) {
        decreaseComment()
        commentsDataBase.document(match.getMatchIdForComment())
            .update(mapOf("${commentDTO.uID}_${commentDTO.systemTime}" to commentDTO.gzipToStr()))
            .addOnSuccessListener {
                emitter.onNext(commentDTO)
                emitter.onComplete()
            }
            .addOnCanceledListener {}
            .addOnFailureListener {
                if (it is FirebaseFirestoreException) {
                    onFirstCommentForMatch(it, match, commentDTO, emitter)
                }
            }
    }

    private fun onFirstCommentForMatch(
        exception: FirebaseFirestoreException,
        match: FootballMatch,
        commentDTO: CommentDTO,
        emitter: ObservableEmitter<CommentDTO>
    ) {
        if (exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {
            commentsDataBase.document(match.getMatchIdForComment())
                .set(mapOf("${commentDTO.uID}_${commentDTO.systemTime}" to commentDTO.gzipToStr()))
                .addOnSuccessListener {
                    emitter.onNext(commentDTO)
                    emitter.onComplete()
                }
                .addOnFailureListener {

                }
        }
    }

    fun cacheCommentNum() {
        ketValueStorage.save("comments", commentNum, Int::class.java)
    }

    fun decreaseComment() {
        --commentNum
        cacheCommentNum()
        commentSource?.onNext(commentNum)
        commentCountDataBase?.setValue(commentNum)
            ?.addOnCanceledListener {
            }?.addOnSuccessListener {
            }?.addOnFailureListener {
            }
    }

    override fun increaseComment(amount: Int) {
        commentNum += amount
        cacheCommentNum()
        commentSource?.onNext(commentNum)
        commentCountDataBase?.setValue(commentNum)
            ?.addOnCanceledListener {
            }?.addOnSuccessListener {
            }?.addOnFailureListener {
            }
    }
    override fun replyCommentFor(comment: CommentDTO, target: CommentDTO, space: CommentSpace) {
        TODO("Not yet implemented")
    }
}