package firstapp.com.pulse;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReceiptList extends ArrayAdapter<Receipt> {
    private Activity context;
    private List<Receipt> receiptList;

    public ReceiptList(Activity context, List<Receipt> ReceiptList){
        super(context, R.layout.list_layout, ReceiptList);
        this.context  = context;
        this.receiptList = ReceiptList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        Receipt Receipt = receiptList.get(position);
        TextView dateView = (TextView) listViewItem.findViewById(R.id.date1);
        TextView departmentView = (TextView) listViewItem.findViewById(R.id.department1);
        TextView doctorView = (TextView) listViewItem.findViewById(R.id.doctor1);
        TextView chargesView = (TextView) listViewItem.findViewById(R.id.charges1);


        dateView.setText(Receipt.timeDate);
        departmentView.setText(Receipt.department);
        doctorView.setText(Receipt.doctor);
        chargesView.setText(Receipt.charge);


        return listViewItem;


    }





}

