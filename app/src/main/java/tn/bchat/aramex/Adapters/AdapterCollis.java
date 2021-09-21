package tn.bchat.aramex.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.bchat.aramex.Models.PackageCollis;
import tn.bchat.aramex.R;

public class AdapterCollis extends RecyclerView.Adapter<AdapterCollis.MyHolder> {

    Context context;
    List<PackageCollis> packageCollisList;

    public AdapterCollis(Context context, List<PackageCollis> packageCollisList){
        this.context = context;
        this.packageCollisList = packageCollisList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_collis, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        //get data
      String firstName = packageCollisList.get(i).getFirstName();
      String lastName = packageCollisList.get(i).getLastName();
      String email = packageCollisList.get(i).getEmail();
      String phone = packageCollisList.get(i).getPhone();

      //set data
        holder.firstNameTv.setText(firstName);
        holder.lastNameTv.setText(lastName);
        holder.emailTv.setText(email);
        holder.phoneTv.setText(phone);

    }

    @Override
    public int getItemCount() {
        return packageCollisList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView firstNameTv, lastNameTv, emailTv, phoneTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            firstNameTv = itemView.findViewById(R.id.firstNameTv);
            lastNameTv = itemView.findViewById(R.id.lastNameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);

        }
    }
}
