package com.kt.apps.xembongda.ui.worldcup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.BuildConfig
import com.kt.apps.xembongda.base.BaseViewModel
import com.kt.apps.xembongda.model.DataState
import com.kt.apps.xembongda.model.League
import com.kt.apps.xembongda.ui.worldcup.model.EuroFootballMatchItem
import com.kt.apps.xembongda.ui.worldcup.model.FootbalMatchTable
import com.kt.apps.xembongda.usecase.ranking.GetListLeagueAvailableRanking
import com.kt.apps.xembongda.usecase.ranking.GetRankingForLeague
import javax.inject.Inject


class WorldCupIterators @Inject constructor(
    val getListLeagueAvailableRanking: GetListLeagueAvailableRanking,
    val getRankingForLeague: GetRankingForLeague
)

class WorldCupViewModel @Inject constructor(
    private val iterator: WorldCupIterators
) : BaseViewModel() {

    init {

    }

    private val _allLeague by lazy { MutableLiveData<DataState<List<League>>>() }
    val allLeague: LiveData<DataState<List<League>>>
        get() = _allLeague

    fun getAllLeague() {
        _allLeague.postValue(DataState.Loading())
        add(
            iterator.getListLeagueAvailableRanking()
                .map {
                    it.subList(0, 1)
                }
                .subscribe({
                    _allLeague.postValue(DataState.Success(it))
                    getRankingForLeague(it[0])
                }, {
                    _allLeague.postValue(DataState.Error(it))
                    if (BuildConfig.DEBUG) {
                        Log.e(this::class.java.simpleName, it.message, it)
                    }
                })
        )
    }

    private val _worldCupRanking by lazy { MutableLiveData<DataState<List<EuroFootballMatchItem>>>() }
    val worldCupRanking: LiveData<DataState<List<EuroFootballMatchItem>>>
        get() = _worldCupRanking

    fun getRankingForLeague(league: League) {
        _worldCupRanking.postValue(DataState.Loading())
        add(
            iterator.getRankingForLeague(league)
                .map {
                    Log.e("Thread", Thread.currentThread().name)
                    it.groupBy {
                        it.table
                    }.map {
                        EuroFootballMatchItem(
                            name = it.key,
                            matchs = listOf(),
                            table = it.value.map {
                                FootbalMatchTable(
                                    it.draw.toString(),
                                    it.totalGoal.toString(),
                                    it.goalDifference.toString(),
                                    (it.totalGoal - it.goalDifference).toString(),
                                    it.lose.toString(),
                                    it.totalPlayed.toString(),
                                    it.score.toString(),
                                    it.win.toString(),
                                    it.team.name,
                                    it.team.logo
                                )
                            }
                        )
                    }
                }
                .subscribe({
                    _worldCupRanking.postValue(DataState.Success(it))
                }, {
                    _worldCupRanking.postValue(DataState.Error(it))
                })
        )
    }


}