package me.lx.sample

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import me.lx.sample.databinding.ActivityMainBinding


private const val STATE_TITLE = "title"
class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)

        val listener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.action_recyclerview -> FragmentRecyclerView()
                else -> {
                    binding.drawerLayout.closeDrawers()
                    return@OnNavigationItemSelectedListener false
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .commit()
            actionBar.title = menuItem.title
            binding.drawerLayout.closeDrawers()
            true
        }
        binding.navView.setNavigationItemSelectedListener(listener)

        if (savedInstanceState == null) {
            listener.onNavigationItemSelected(binding.navView.menu.getItem(0))
        } else {
            actionBar.title = savedInstanceState.getCharSequence(STATE_TITLE)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return toggle.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(STATE_TITLE, supportActionBar!!.title)
    }
}