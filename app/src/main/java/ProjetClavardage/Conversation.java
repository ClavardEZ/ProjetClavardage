import java.util.ArrayList;
import java.util.List;
import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("cc738ca0-8467-47fa-877d-006a26ef9ccd")
public class Conversation {
    @objid ("be982b42-3baa-415f-a338-77946714bd71")
    public List<User> users = new ArrayList<User> ();

    @objid ("7da8a105-f291-4db4-a8e5-2471b1e526c4")
    public List<Message> messages = new ArrayList<Message> ();

    @objid ("99632b5c-fb5e-43de-8b11-5ebd751f28c1")
    public Conversation(List<User> users) {
    }

    @objid ("8095ff78-1db7-4d40-a0b8-46b009f4e108")
    public Conversation() {
    }

    @objid ("35fe9fbd-a626-4683-bcf9-e62fb62e2888")
    public void addUser(User user) {
    }

    @objid ("2ed5c99e-859b-4682-9523-d7b5fec1e0aa")
    public void removeUser(User user) {
    }

}
