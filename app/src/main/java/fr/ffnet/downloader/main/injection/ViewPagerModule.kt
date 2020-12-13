package fr.ffnet.downloader.main.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.main.MainActivity
import fr.ffnet.downloader.main.ViewPagerViewModel
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class ViewPagerModule(private val activity: MainActivity) {

    @Provides
    fun provideViewPagerViewModel(
        databaseRepository: DatabaseRepository,
        authorRepository: AuthorRepository
    ): ViewPagerViewModel {
        val factory = ViewModelFactory { ViewPagerViewModel(databaseRepository, authorRepository) }
        return ViewModelProvider(activity, factory)[ViewPagerViewModel::class.java]
    }
}
