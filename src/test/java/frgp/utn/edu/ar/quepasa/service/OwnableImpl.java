package frgp.utn.edu.ar.quepasa.service;

import frgp.utn.edu.ar.quepasa.model.User;

public class OwnableImpl implements frgp.utn.edu.ar.quepasa.model.Ownable {

    private User owner;
    /**
     */
    @Override
    public User getOwner() {
        return owner;
    }

    /**
     */
    @Override
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
