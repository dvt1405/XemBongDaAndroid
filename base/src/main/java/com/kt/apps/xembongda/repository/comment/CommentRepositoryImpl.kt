package com.kt.apps.xembongda.repository.comment

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kt.apps.xembongda.model.FootballMatch
import com.kt.apps.xembongda.model.comments.CommentDTO
import com.kt.apps.xembongda.model.comments.CommentSpace
import com.kt.apps.xembongda.repository.ICommentRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor() : ICommentRepository {
    private val commentsDataBase by lazy {
        Firebase.firestore.collection("comments")
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
            Log.e("TAG", "On cancelable")
            task.remove()
        }
    }

    override fun sendCommentsFor(comment: CommentDTO, space: CommentSpace): Observable<CommentDTO> {
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

    override fun replyCommentFor(comment: CommentDTO, target: CommentDTO, space: CommentSpace) {
        TODO("Not yet implemented")
    }
}