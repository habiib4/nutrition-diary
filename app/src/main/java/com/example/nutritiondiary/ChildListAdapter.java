package com.example.nutritiondiary;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ViewHolder> {


    public List<ChildList> childLists;

    Context context;

    String calories = "100";

    boolean exp = false;

    public ChildListAdapter(List<ChildList> childLists) {
        this.childLists = childLists;
    }

    @NonNull
    @Override
    public ChildListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_model,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildListAdapter.ViewHolder holder, final int position) {


        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        holder.name.setText(childLists.get(position).getName());
        holder.age.setText(String.valueOf(childLists.get(position).getAge())+" years");
        holder.weight.setText(String.valueOf(childLists.get(position).getWeight())+" kg");
        holder.bmi.setText(String.valueOf(childLists.get(position).getBmi()));

        RequestOptions placeholderReq = new RequestOptions();
        placeholderReq.placeholder(R.drawable.load);

        context = holder.mView.getContext();

        final String Childid = childLists.get(position).PostID;

        final String parentid = childLists.get(position).PostID3;

        final double totalcal = childLists.get(position).calories;

        holder.image.setClipToOutline(true);

        Glide.with(holder.mView.getContext()).setDefaultRequestOptions(placeholderReq).load(childLists.get(position).getImagepath()).into(holder.image);


        holder.idealweight.setText(String.valueOf(childLists.get(position).getIdealweight())+" kg");

        holder.height.setText((int)childLists.get(position).getHeightft()+ " ft " +(int)childLists.get(position).getHeightinch()+" inch");

        holder.fatper.setText(childLists.get(position).getFatpercentage()+"%");

        if((childLists.get(position).getWeight() - childLists.get(position).getIdealweight()) >5){

            holder.happy.setImageResource(R.drawable.sad);

        }else if ((childLists.get(position).getIdealweight() - childLists.get(position).getWeight()) >5){

            holder.happy.setImageResource(R.drawable.sad);
        }else{

            holder.happy.setImageResource(R.drawable.happy);
        }


        DocumentReference docRef = FirebaseFirestore.getInstance().collection("parents").document(parentid).
                collection("Childs").document(Childid).collection("Diary").document("2019330");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    final String cal = snapshot.getString("totalcal");

                    List<PieEntry> pieEntries = new ArrayList<>();

                    PieDataSet dataSet = new PieDataSet(pieEntries,"");

                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(3f);

                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    pieEntries.add(new PieEntry((float) (totalcal-Integer.parseInt(cal)),"Remaining"));

                    pieEntries.add(new PieEntry(Integer.parseInt(cal),"Consumed"));



                    PieData pieData = new PieData(dataSet);

                    holder.pieChart.setUsePercentValues(false);
                    holder.pieChart.setDrawHoleEnabled(true);
                    holder.pieChart.setDragDecelerationFrictionCoef(0.99f);
                    holder.pieChart.setCenterText(String.valueOf(childLists.get(position).getCalories()));
                    holder.pieChart.getDescription().setEnabled(false);

                    holder.pieChart.setDrawEntryLabels(false);

                    Legend legend = holder.pieChart.getLegend();

                    legend.setMaxSizePercent(90);
                    legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                    legend.setTextSize(10f);


                    holder.pieChart.animateY(5000, Easing.EasingOption.EaseOutBack);


                    pieData.setValueTextSize(11f);

                    holder.pieChart.setData(pieData);
                    holder.pieChart.invalidate();


                } else {


                    //final String cal = snapshot.getString("calorie");

                    List<PieEntry> pieEntries = new ArrayList<>();

                    PieDataSet dataSet = new PieDataSet(pieEntries,"");

                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(3f);

                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                    pieEntries.add(new PieEntry((float) totalcal,"Remaining"));

                    pieEntries.add(new PieEntry(0,"Consumed"));



                    PieData pieData = new PieData(dataSet);

                    holder.pieChart.setUsePercentValues(false);
                    holder.pieChart.setDrawHoleEnabled(true);
                    holder.pieChart.setDragDecelerationFrictionCoef(0.99f);
                    holder.pieChart.setCenterText(String.valueOf(childLists.get(position).getCalories()));
                    holder.pieChart.getDescription().setEnabled(false);

                    holder.pieChart.setDrawEntryLabels(false);

                    Legend legend = holder.pieChart.getLegend();

                    legend.setMaxSizePercent(90);
                    legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                    legend.setTextSize(10f);


                    holder.pieChart.animateY(5000, Easing.EasingOption.EaseOutBack);


                    pieData.setValueTextSize(11f);

                    holder.pieChart.setData(pieData);
                    holder.pieChart.invalidate();


                }
            }
        });





        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DiaryActivity.class);
                intent.putExtra("childid", Childid);
                intent.putExtra("parentid", parentid);
                context.startActivity(intent);

            }
        });



//        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(context, DiaryActivity.class);
//                intent.putExtra("postid", postID);
//                context.startActivity(intent);
//
//            }
//        });


        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(holder.expand.getVisibility() == View.VISIBLE){


                    holder.expand.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    holder.expand.setVisibility(View.GONE);
                                }
                            });;

                }else{

                    holder.expand.setAlpha(0f);
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.expand.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return childLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView name;
        TextView age;
        TextView bmi;
        TextView weight;
        ImageView image;
        TextView height;
        TextView idealweight;
        TextView fatper;
        PieChart pieChart;

        ImageView happy,details;

        ConstraintLayout expand;

        ConstraintLayout main, constraintLayout,cons2;

        public ViewHolder(View itemView) {
            super(itemView);


            mView = itemView;

            main = (ConstraintLayout) mView.findViewById(R.id.cardView);

            expand = (ConstraintLayout) mView.findViewById(R.id.expand);

            pieChart = (PieChart)mView.findViewById(R.id.chart);
            name = (TextView)mView.findViewById(R.id.childName);
            age = (TextView) mView.findViewById(R.id.age);
            bmi = (TextView) mView.findViewById(R.id.bmi);
            weight = (TextView) mView.findViewById(R.id.weight);

            height = (TextView) mView.findViewById(R.id.height);
            fatper = (TextView) mView.findViewById(R.id.fatper);
            idealweight = (TextView) mView.findViewById(R.id.idealweight);

            image = (ImageView) mView.findViewById(R.id.childImage);

            happy = (ImageView)mView.findViewById(R.id.happy);
            details = (ImageView)mView.findViewById(R.id.details);

            //constraintLayout = (ConstraintLayout)mView.findViewById(R.id.constraintLayout2);


        }
    }
}
