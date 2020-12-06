package fr.ffnet.downloader.main.search.injection

import dagger.Subcomponent
import fr.ffnet.downloader.main.search.SearchFragment

@Subcomponent(
    modules = [
        SearchModule::class
    ]
)
interface SearchComponent {
    fun inject(fragment: SearchFragment)
}
