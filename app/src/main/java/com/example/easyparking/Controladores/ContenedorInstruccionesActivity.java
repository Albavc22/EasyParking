package com.example.easyparking.Controladores;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.easyparking.R;

public class ContenedorInstruccionesActivity extends AppCompatActivity implements IntroduccionFragment.OnFragmentInteractionListener,
        InstruccionAjustesFragment.OnFragmentInteractionListener, InstruccionAyudaFragment.OnFragmentInteractionListener, InstruccionEstacionamientosCursoFragment.OnFragmentInteractionListener,
        InstruccionIniciarEstacionamientoFragment.OnFragmentInteractionListener,
        InstruccionPerfilFragment.OnFragmentInteractionListener, InstruccionVehiculosFragment.OnFragmentInteractionListener, InstruccionEstacionamientoParkingsFragment.OnFragmentInteractionListener {

    private SectionPagerAdapter sectionPagerAdapter;
    private ViewPager viewPager;
    private LinearLayout linearPuntos;
    private TextView[] puntosSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_instrucciones);

        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionPagerAdapter);

        linearPuntos = findViewById(R.id.idLinearPuntos);
        agregarIndicadorPuntos(0);

        viewPager.addOnPageChangeListener(viewListener);
    }

    private void agregarIndicadorPuntos(int pos) {
        puntosSlide = new TextView[8];
        linearPuntos.removeAllViews();

        for (int i=0; i<puntosSlide.length; i++) {
            puntosSlide[i] = new TextView(this);
            puntosSlide[i].setText(Html.fromHtml("&#8226;"));
            puntosSlide[i].setTextSize(35);
            puntosSlide[i].setTextColor(getResources().getColor(R.color.colorBlancoTransparente));
            linearPuntos.addView(puntosSlide[i]);
        }

        if (puntosSlide.length>0) {
            puntosSlide[pos].setTextColor(getResources().getColor(R.color.colorBlanco));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            agregarIndicadorPuntos(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

        }

        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;

            switch (sectionNumber) {
                case 1:
                    fragment = new IntroduccionFragment();
                    break;
                case 2:
                    fragment = new InstruccionIniciarEstacionamientoFragment();
                    break;
                case 3:
                    fragment = new InstruccionEstacionamientosCursoFragment();
                    break;
                case 4:
                    fragment = new InstruccionVehiculosFragment();
                    break;
                case 5:
                    fragment = new InstruccionEstacionamientoParkingsFragment();
                    break;
                case 6:
                    fragment = new InstruccionAjustesFragment();
                    break;
                case 7:
                    fragment = new InstruccionPerfilFragment();
                    break;
                case 8:
                    fragment = new InstruccionAyudaFragment();
                    break;
            }

            return fragment;
        }
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 8;
        }
    }
}