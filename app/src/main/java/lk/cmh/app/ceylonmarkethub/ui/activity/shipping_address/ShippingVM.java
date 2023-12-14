package lk.cmh.app.ceylonmarkethub.ui.activity.shipping_address;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import javax.annotation.Nullable;

import kotlin.jvm.functions.Function1;
import lk.cmh.app.ceylonmarkethub.CMH;
import lk.cmh.app.ceylonmarkethub.data.model.user.UserAddress;
import lk.cmh.app.ceylonmarkethub.data.repository.UserRepository;
import lk.cmh.app.ceylonmarkethub.data.util.RetrofitUtil;
import lk.cmh.app.ceylonmarkethub.ui.activity.orders.OrderConfirmationVM;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingVM extends ViewModel {

    private static final String TAG = ShippingVM.class.getSimpleName();

    private MutableLiveData<ShippingFormState> shippingFormState = new MutableLiveData<>();

    private UserRepository userRepository;

    private MutableLiveData<UserAddress> userAddress = new MutableLiveData<>();

    public ShippingVM(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static final ViewModelInitializer<ShippingVM> initializer = new ViewModelInitializer<>(ShippingVM.class, new Function1<CreationExtras, ShippingVM>() {
        @Override
        public ShippingVM invoke(CreationExtras creationExtras) {
            CMH instance = CMH.getInstance();
            SharedPreferences auth = instance.getSharedPreferences("auth", Context.MODE_PRIVATE);
            UserRepository repository = RetrofitUtil.getInstance().getRepository(UserRepository.class, auth);
            return new ShippingVM(repository);
        }
    });

    public void shippingAddressDataChanged(String contactName, String mobileNumber, @Nullable String addressLine1, String province, String city, String zipCode) {
        ShippingFormState shippingFS = shippingFormState.getValue();
        if (shippingFS == null) {
            shippingFS = new ShippingFormState();
        }

        if (contactName.isEmpty()) {
            shippingFS.setContactNameError("Contact name cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else {
            shippingFS.setContactNameError(null);
        }

        if (mobileNumber.isEmpty()) {
            shippingFS.setMobileNumberError("Mobile number cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else if (mobileNumber.length() < 10) {
            shippingFS.setMobileNumberError("Mobile number should be 10 digits");
            shippingFormState.setValue(shippingFS);
        }else{
            shippingFS.setMobileNumberError(null);
        }

        if (addressLine1.isEmpty()) {
            shippingFS.setAddressLine1Error("Address Line 1 cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else {
            shippingFS.setAddressLine1Error(null);
        }

        if (province.isEmpty()) {
            shippingFS.setProvinceError("Province cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else {
            shippingFS.setProvinceError(null);
        }

        if (city.isEmpty()) {
            shippingFS.setCityError("City cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else {
            shippingFS.setCityError(null);
        }

        if (zipCode.isEmpty()) {
            shippingFS.setZipCodeError("Zipcode cannot be empty");
            shippingFormState.setValue(shippingFS);
        }else {
            shippingFS.setZipCodeError(null);
        }

        if (!contactName.isEmpty() && !mobileNumber.isEmpty() && !addressLine1.isEmpty() && !province.isEmpty() && !city.isEmpty() && !zipCode.isEmpty()) {
            shippingFormState.setValue(new ShippingFormState(true));
        }
    }

    public MutableLiveData<ShippingFormState> getShippingFormState() {
        return shippingFormState;
    }

    public void save(String contactName, String mobileNumber, String addressLine1, String addressLine2, String province, String city, String zipCode) {
        UserAddress address = new UserAddress(contactName, addressLine1, addressLine2, city, zipCode, province, mobileNumber);
        userRepository.updateAddress(address).enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                userAddress.setValue(response.body());
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loadAddress() {
        userRepository.getAddress().enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                userAddress.setValue(response.body());
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<UserAddress> getUserAddress() {
        return userAddress;
    }
}
