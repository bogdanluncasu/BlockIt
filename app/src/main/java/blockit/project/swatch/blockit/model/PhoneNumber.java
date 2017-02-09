package blockit.project.swatch.blockit.model;

import java.io.Serializable;

/**
 * Created by swatch on 12/17/16.
 */
public class PhoneNumber implements Serializable {
    Integer id;
    String phoneNumber;
    public PhoneNumber(Integer id, String phoneNumber){
        this.id=id;
        this.phoneNumber=phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getId() {
        return id;
    }
}
