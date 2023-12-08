package lk.cmh.app.ceylonmarkethub.data.adapter.lv;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import lk.cmh.app.ceylonmarkethub.R;
import lk.cmh.app.ceylonmarkethub.data.model.settings.SettingsItem;
import lk.cmh.app.ceylonmarkethub.ui.activity.settings.user_profile.UserProfileActivity;
import lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address.ShippingAddressActivity;

public class SettingsLvAdapter extends ArrayAdapter<SettingsItem> {

    private static final String TAG = SearchLvAdapter.class.getSimpleName();

    public SettingsLvAdapter(@NonNull Context context, List<SettingsItem> settingsItems) {
        super(context, 0, settingsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_settings_item, parent, false);
        }

        SettingsItem settingItem = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.settingTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.settingDescription);

        titleTextView.setText(settingItem.getTitle());
        descriptionTextView.setText(settingItem.getDescription());

        convertView.setOnClickListener(v -> {
            if(settingItem.getActivity() == ShippingAddressActivity.class) {
                Intent intent = new Intent(getContext(), settingItem.getActivity());
                intent.putExtra("context", "settings");
                getContext().startActivity(intent);
            }else if(settingItem.getActivity() == UserProfileActivity.class) {
                getContext().startActivity(new Intent(getContext(), settingItem.getActivity()));
            }
        });

        return convertView;
    }
}
