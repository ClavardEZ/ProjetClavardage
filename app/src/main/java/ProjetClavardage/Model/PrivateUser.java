package ProjetClavardage.Model;

import com.modeliosoft.modelio.javadesigner.annotations.objid;

@objid ("522fe6a2-34d6-47dd-aa85-8a9a8745151c")
public class PrivateUser extends User {
    @objid ("dfda4519-8ba4-45dc-8117-a2bb78b23a54")
    private String password;

    @objid ("02493165-31ae-47d1-b584-1f2c87f1cbaa")
    public void updateUsername(String pseudo) {
    }

    @objid ("8a18903b-2254-45a0-8802-caa685c57cf9")
    public void setPassword(String password) {
    }

    @objid ("f52deadf-bfe3-4ae0-b788-b8115b96d0cb")
    public PrivateUser() {
    }

}
