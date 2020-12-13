package fr.ffnet.downloader.author.injection

import dagger.Subcomponent
import fr.ffnet.downloader.author.AuthorActivity

@Subcomponent(
    modules = [
        AuthorModule::class
    ]
)
interface AuthorComponent {
    fun inject(activity: AuthorActivity)
}
