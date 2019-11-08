package cl.app.autismo_rancagua.Vista.UI

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import cl.app.autismo_rancagua.R
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_splash.*


class Principal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        cargar_animaciones()


        btn_administrador.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.putExtra("tipo_usuario", "ADMINISTRADOR")
            startActivity(intent)
        }

        btn_profesional.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            intent.putExtra("tipo_usuario", "PROFESIONAL")
            startActivity(intent)
        }


    }


    override fun onResume() {
        super.onResume()
        cargar_animaciones()

    }

    private fun cargar_animaciones() {
        val smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig)
        val fleft = AnimationUtils.loadAnimation(this, R.anim.fleft)
        val fhelper = AnimationUtils.loadAnimation(this, R.anim.fhelper)
        val stb2 = AnimationUtils.loadAnimation(this, R.anim.stb2)

        ivSplash.startAnimation(smalltobig)

        ivLogo.setTranslationY(400f)
        ivSubtitle.setTranslationX(400f)
        ivSubtitle2.setTranslationX(400f)
        btn_administrador.setTranslationX(400f)
        btn_profesional.setTranslationX(400f)
        vector.setTranslationY(400f)

        ivLogo.setAlpha(0f)
        ivSubtitle.setAlpha(0f)
        ivSubtitle2.setAlpha(0f)
        btn_administrador.setAlpha(0f)
        btn_profesional.setAlpha(0f)
        vector.setAlpha(0f)

        ivLogo.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
        ivSubtitle.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(700).start()
        ivSubtitle2.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(700).start()
        btn_administrador.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(200).start()
        btn_profesional.animate().translationX(0f).alpha(1f).setDuration(800).setStartDelay(400).start()
        vector.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
    }

}
