package com.github.ghostbusters.ghosthouse.home.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.ghostbusters.ghosthouse.R;
import com.github.ghostbusters.ghosthouse.loggin.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import static com.github.ghostbusters.ghosthouse.home.Home.KEY_NAME;
import static com.github.ghostbusters.ghosthouse.home.Home.PREFS_NAME;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(final String param1, final String param2) {
        final UserFragment fragment = new UserFragment();
        final Bundle args = new Bundle();
        args.putString(UserFragment.ARG_PARAM1, param1);
        args.putString(UserFragment.ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }



    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.mParam1 = this.getArguments().getString(UserFragment.ARG_PARAM1);
            this.mParam2 = this.getArguments().getString(UserFragment.ARG_PARAM2);
        }

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        this.mGoogleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME,getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        final boolean isNightMode = settings.getBoolean(KEY_NAME, false);
        Log.d(TAG,String.format("¿Night Mode? %s", String.valueOf(isNightMode)));

        int theme;

        if (isNightMode){
            theme=R.style.AppDarkTheme;
        }else{
            theme=R.style.AppTheme;
        }
        Log.v(UserFragment.TAG, String.valueOf(theme));
        //Set the theme
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(),theme);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ////////////////
        // Inflate the layout for this fragment
        final View view = localInflater.inflate(R.layout.fragment_user, container, false);




        final TextView name = (TextView) view.findViewById(R.id.textView);
        final TextView email = (TextView) view.findViewById(R.id.textViewEmail);
        final ImageView pic = (ImageView) view.findViewById(R.id.imageView);

        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this.getActivity());
        if (acct != null) {
            final String personName = acct.getDisplayName();
            final String personGivenName = acct.getGivenName();
            final String personFamilyName = acct.getFamilyName();
            final String personEmail = acct.getEmail();
            final String personId = acct.getId();
            final Uri personPhoto = acct.getPhotoUrl();
            String personPhotoUrl = acct.getPhotoUrl().toString();

            name.setText(personName);
            email.setText(personEmail);
            Log.v(UserFragment.TAG, personPhoto.toString());
//            pic.setImageBitmap(
//                    decodeSampledBitmapFromResource(getResources(), R.id.imageView,pic.getMaxWidth(), pic.getMaxHeight()));
//            Glide.with(getContext()).load(personPhotoUrl).into(pic);
            Transformation transformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    int size = Math.min(source.getWidth(), source.getHeight());

                    int x = (source.getWidth() - size) / 2;
                    int y = (source.getHeight() - size) / 2;

                    Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
                    if (squaredBitmap != source) {
                        source.recycle();
                    }

                    Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    BitmapShader shader = new BitmapShader(squaredBitmap,
                            BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
                    paint.setShader(shader);
                    paint.setAntiAlias(true);

                    float r = size / 2f;
                    canvas.drawCircle(r, r, r, paint);

                    squaredBitmap.recycle();
                    return bitmap;
                }

                @Override
                public String key() {
                    return "circle";
                }
            };

            Picasso.with(getContext())
                    .load(personPhoto)
                    .placeholder(R.drawable.ic_person_black_50dp)
                    .error(R.drawable.ic_person_black_50dp)
                    .transform(transformation)
                    .into(pic);

        }


        final Button button = (Button) view.findViewById(R.id.buttonLogOut);
        button.setOnClickListener(new View.OnClickListener() {

            private void signOut() {
                UserFragment.this.mGoogleSignInClient.signOut()
                        .addOnCompleteListener(UserFragment.this.getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull final Task<Void> task) {
                                final Intent intent = new Intent(UserFragment.this.getActivity(), LoginActivity.class);
                                UserFragment.this.startActivity(intent);
                            }
                        });
            }

            @Override
            public void onClick(final View v) {
                Log.v(UserFragment.TAG, "Se ha pulsado el botón de Logout");
                this.signOut();
            }
        });

        final Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        switch1.setChecked(isNightMode);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.v(UserFragment.TAG, "Switch activado");
                    editor.putBoolean(KEY_NAME, true);
                    editor.commit();
                    mListener.setGhostTheme(R.style.AppDarkTheme);
//                    getActivity().setTheme(R.style.AppDarkTheme);


                } else {
                    // The toggle is disabled
                    Log.v(UserFragment.TAG, "Switch desactivado");
                    editor.putBoolean(KEY_NAME, false);
                    editor.commit();
                    mListener.setGhostTheme(R.style.AppTheme);
//                    getActivity().setTheme(R.style.AppTheme);
                }

//                getActivity().recreate();
//
            }
        });

        final CheckBox checkbox1 = (CheckBox) view.findViewById(R.id.checkBox1);

        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {

                if (isChecked) {
                    // The toggle is enabled
                    Log.v(UserFragment.TAG, "Checkbox1 activado");

                } else {
                    // The toggle is disabled
                    Log.v(UserFragment.TAG, "Checkbox1 desactivado");

                }

            }
        });

        final CheckBox checkbox2 = (CheckBox) view.findViewById(R.id.checkBox2);
        checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Log.v(UserFragment.TAG, "Checkbox2 activado");
                } else {
                    // The toggle is disabled
                    Log.v(UserFragment.TAG, "Checkbox2 desactivado");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(final Uri uri) {
        if (this.mListener != null) {
            this.mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.mListener = (OnFragmentInteractionListener) context;
        } else {
            Log.d(UserFragment.class.getName(), "USER ATTACHED");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.d("Connect", "failed ");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        int setGhostTheme(int t);
    }
}
