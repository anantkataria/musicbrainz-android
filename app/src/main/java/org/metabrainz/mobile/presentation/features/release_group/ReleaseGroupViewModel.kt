package org.metabrainz.mobile.presentation.features.release_group

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import org.metabrainz.mobile.data.repository.LookupRepository
import org.metabrainz.mobile.data.sources.api.entities.WikiSummary
import org.metabrainz.mobile.data.sources.api.entities.mbentity.MBEntityType
import org.metabrainz.mobile.data.sources.api.entities.mbentity.ReleaseGroup
import org.metabrainz.mobile.presentation.features.LookupViewModel
import org.metabrainz.mobile.util.Resource

class ReleaseGroupViewModel @ViewModelInject constructor(repository: LookupRepository?) : LookupViewModel(repository!!, MBEntityType.RELEASE_GROUP) {
    val wikiData: LiveData<Resource<WikiSummary>>
    override val data: LiveData<Resource<ReleaseGroup>>
    private fun toReleaseGroup(data: Resource<String>?): Resource<ReleaseGroup> {
        val resource: Resource<ReleaseGroup>
        resource = try {
            if (data != null && data.status == Resource.Status.SUCCESS) {
                val releaseGroup = Gson().fromJson(data.data, ReleaseGroup::class.java)
                Resource(Resource.Status.SUCCESS, releaseGroup)
            } else Resource.getFailure(ReleaseGroup::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.getFailure(ReleaseGroup::class.java)
        }
        return resource
    }

    private suspend fun fetchWikiSummary(resource: Resource<ReleaseGroup>): Resource<WikiSummary> {
        if (resource.status == Resource.Status.SUCCESS) {
            var title = ""
            var method = -1
            for (link in resource.data.relations) {
                if (link.type == "wikipedia") {
                    title = link.pageTitle
                    method = LookupRepository.METHOD_WIKIPEDIA_URL
                    break
                }
                if (link.type == "wikidata") {
                    title = link.pageTitle
                    method = LookupRepository.METHOD_WIKIDATA_ID
                    break
                }
            }
            if (title.isNotEmpty())
                return repository.fetchWikiSummary(title, method)
        }
        return Resource.getFailure(WikiSummary::class.java)
    }

    init {
        data = jsonLiveData.map { toReleaseGroup(it) }
        wikiData = data.switchMap {
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                emit(fetchWikiSummary(it))
            }
        }
    }
}