package fr.ffnet.downloader.repository

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RegularCrawlService {
    @GET("s/{storyId}/{chapterId}")
    fun getFanfiction(
        @Path("storyId") storyId: String,
        @Path("chapterId") chapterId: String? = "1"
    ): Call<ResponseBody>

    @GET("u/{profileId}")
    fun getProfile(@Path("profileId") profileId: String): Call<ResponseBody>

    @GET("search/?ready=1&ready=1")
    fun search(
        @Query("keywords") keywords: String,
        @Query("type") searchType: String
    ): Call<ResponseBody>

    @GET("r/{fanfictionId}/")
    fun getReviews(@Path("fanfictionId") fanfictionId: String): Call<ResponseBody>

    @GET("j/0/1/0/")
    fun justInPublished(): Call<ResponseBody>

    @GET("j/0/2/0/")
    fun justInUpdated(): Call<ResponseBody>
}
