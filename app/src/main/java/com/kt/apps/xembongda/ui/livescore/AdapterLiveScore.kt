package com.kt.apps.xembongda.ui.livescore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.kt.apps.xembongda.R
import com.kt.apps.xembongda.model.LiveScoreDTO
import com.kt.apps.xembongda.utils.loadImage

sealed class ILiveScoreModel {
    class Ads(
        val nativeAd: NativeAd
    ) : ILiveScoreModel()

    class LiveScore(
        val model: LiveScoreDTO.Match
    ) : ILiveScoreModel()

    class LiveScoreTitle(val title: LiveScoreDTO.Title) : ILiveScoreModel()
}

class AdapterLiveScore : RecyclerView.Adapter<AdapterLiveScore.ViewHolder>(), PinItemDecoration.Listener {
    private val _listItem by lazy {
        mutableListOf<ILiveScoreModel>()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val holder: View
            get() = itemView
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (_listItem[position]) {
            is ILiveScoreModel.Ads -> R.layout.item_live_score_ads
            is ILiveScoreModel.LiveScoreTitle -> R.layout.item_live_scores_title
            is ILiveScoreModel.LiveScore -> R.layout.item_live_scores
            else -> throw IllegalStateException("Not support view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = _listItem[position]) {
            is ILiveScoreModel.Ads -> {
                bindAds(item, holder.itemView)
            }

            is ILiveScoreModel.LiveScoreTitle -> {
                bindTitle(item, holder.itemView)
            }

            else -> {
                bindItems(item as ILiveScoreModel.LiveScore, holder.itemView)
            }
        }
    }

    private fun bindTitle(item: ILiveScoreModel.LiveScoreTitle, view: View) {
        val titleView = view.findViewById<TextView>(R.id.title)
        titleView.text = item.title.league
    }

    private fun bindAds(item: ILiveScoreModel.Ads, view: View) {

    }

    fun onRefresh(list: List<ILiveScoreModel>) {
        _listItem.clear()
        _listItem.addAll(list)
        notifyDataSetChanged()
    }

    private fun bindItems(liveScore: ILiveScoreModel.LiveScore, view: View) {
        val homeName = view.findViewById<TextView>(R.id.homeName)
        val homeLogo = view.findViewById<ImageView>(R.id.avatarHomeTeam)
        val awayName = view.findViewById<TextView>(R.id.awayName)
        val awayLogo = view.findViewById<ImageView>(R.id.avatarAwayTeam)
        val score = view.findViewById<TextView>(R.id.score)
        val time = view.findViewById<TextView>(R.id.time)

        homeName.text = liveScore.model.homeTea?.name
        awayName.text = liveScore.model.awayTeam?.name
        score.text = liveScore.model.score
        time.text = "${liveScore.model.time} - ${liveScore.model.date}"
        homeLogo.loadImage(liveScore.model.homeTea?.logo)
        awayLogo.loadImage(liveScore.model.awayTeam?.logo)
    }

    override fun getItemCount(): Int = _listItem.size

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        do {
            if (isHeader(position)) {
                break
            }
            position--
        } while (position >= 0)
        return position
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.item_live_scores_title

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        header?.let {
            bindTitle(_listItem[headerPosition] as ILiveScoreModel.LiveScoreTitle, it)
        }
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return _listItem[itemPosition] is ILiveScoreModel.LiveScoreTitle
    }
}