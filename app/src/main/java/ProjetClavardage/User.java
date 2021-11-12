import com.modeliosoft.modelio.javadesigner.annotations.mdl;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("ea18dc67-19fb-415a-addc-fe6308111f52")
public class User {
    @objid ("3818755d-bb7b-465c-a466-0e6fb13106b0")
    private String username;

    @objid ("6daeff14-70ab-4346-ae62-4c56b20c8784")
    private String login;

    @mdl.prop
    @objid ("b0301632-95b8-48cc-a884-ad67ebf7ff46")
    private boolean isConnected;

    @mdl.propgetter
    private boolean isIsConnected() {
        // Automatically generated method. Please do not modify this code.
        return this.isConnected;
    }

    @mdl.propsetter
    private void setIsConnected(boolean value) {
        // Automatically generated method. Please do not modify this code.
        this.isConnected = value;
    }

    @objid ("6d88105d-86bd-423a-9542-d18d643ccc89")
    public User() {
    }

    @objid ("0993624f-9a9b-4808-9784-67d1f3b67742")
    public User(String login) {
    }

    @objid ("f2099093-09fc-4d01-8162-c19252879382")
    public void setUsername(String username) {
    }

}
