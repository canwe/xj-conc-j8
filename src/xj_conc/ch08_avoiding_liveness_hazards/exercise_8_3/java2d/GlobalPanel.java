/*
 * @(#)GlobalPanel.java	1.20 05/11/30
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)GlobalPanel.java	1.20 05/11/30
 */


package xj_conc.ch08_avoiding_liveness_hazards.exercise_8_3.java2d;

import xj_conc.ch08_avoiding_liveness_hazards.exercise_8_3.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;

import static xj_conc.ch08_avoiding_liveness_hazards.exercise_8_3.Java2Demo.*;


/**
 * Panel that holds the Demo groups, Controls and Monitors for each tab.
 * It's a special "always visible" panel for the Controls, MemoryMonitor &
 * PerformanceMonitor.
 */
public class GlobalPanel extends JPanel implements ChangeListener {


    private JPanel p;
    private int index;


    public GlobalPanel() {
        setLayout(new BorderLayout());
        p = new JPanel(new GridBagLayout());
        EmptyBorder eb = new EmptyBorder(5, 0, 5, 5);
        BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
        p.setBorder(new CompoundBorder(eb, bb));
        Java2Demo.addToGridBag(p, Java2Demo.controls, 0, 0, 1, 1, 0, 0);
        Java2Demo.addToGridBag(p, memorymonitor, 0, 1, 1, 1, 0, 0);
        Java2Demo.addToGridBag(p, Java2Demo.performancemonitor, 0, 2, 1, 1, 0, 0);
        add(Java2Demo.intro);
    }


    public void stateChanged(ChangeEvent e) {

        Java2Demo.group[index].shutDown(Java2Demo.group[index].getPanel());
        if (Java2Demo.tabbedPane.getSelectedIndex() == 0) {
            memorymonitor.surf.stop();
            Java2Demo.performancemonitor.surf.stop();
            removeAll();
            add(Java2Demo.intro);
            Java2Demo.intro.start();
        } else {
            if (getComponentCount() == 1) {
                Java2Demo.intro.stop();
                remove(Java2Demo.intro);
                add(p, BorderLayout.EAST);
                if (Java2Demo.memoryCB.getState()) {
                    memorymonitor.surf.start();
                }
                if (Java2Demo.perfCB.getState()) {
                    Java2Demo.performancemonitor.surf.start();
                }
            } else {
                remove(Java2Demo.group[index]);
            }
            index = Java2Demo.tabbedPane.getSelectedIndex() - 1;
            add(Java2Demo.group[index]);
            Java2Demo.group[index].setup(false);
        }
        revalidate();
    }
}
