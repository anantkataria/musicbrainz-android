package org.metabrainz.mobile.data.sources.api.entities.mbentity

import com.google.gson.annotations.SerializedName
import org.metabrainz.mobile.data.sources.api.entities.userdata.Rating
import org.metabrainz.mobile.data.sources.api.entities.userdata.Tag
import org.metabrainz.mobile.data.sources.api.entities.userdata.UserRating
import org.metabrainz.mobile.data.sources.api.entities.userdata.UserTag
import java.io.Serializable
import java.util.*

open class MBEntity : Serializable {
    @SerializedName("id")
    open var mbid: String? = null
    var disambiguation: String? = null

    @SerializedName("user-rating")
    var userRating: UserRating? = null
    var rating: Rating? = null

    @SerializedName("user-tags")
    var userTags: List<UserTag> = ArrayList()
    var tags: List<Tag> = ArrayList()

    @SerializedName("user-genres")
    var userGenres: List<UserTag> = ArrayList()
    var genres: List<Tag> = ArrayList()
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is MBEntity) return false
        return mbid == o.mbid
    }

    override fun hashCode(): Int {
        return mbid.hashCode()
    }
}