package com.pilzbros.Alcatraz.Manager;

import com.pilzbros.Alcatraz.Objects.JoinSign;
import com.pilzbros.Alcatraz.Objects.Prison;
import org.bukkit.Location;

import java.util.ArrayList;

/**
 * Created by austinpilz on 8/4/16.
 */
public class JoinSignManager {

    private Prison prison;
    private ArrayList<JoinSign> joinSigns;

    public JoinSignManager(Prison p)
    {
        prison = p;
        this.joinSigns = new ArrayList<JoinSign>();
    }

    public void autoCheck()
    {
        for(JoinSign sign : this.joinSigns)
        {
            sign.update();
        }
    }

    public void addJoinSign(Location l)
    {
        JoinSign newJoinSign = new JoinSign(prison, l);
        this.joinSigns.add(newJoinSign);
        newJoinSign.update();
        newJoinSign.saveToDB();
    }
}
