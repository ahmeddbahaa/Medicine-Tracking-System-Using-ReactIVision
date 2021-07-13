import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import TUIO.*;
public class TuioPanelComponent extends  JComponent implements TuioListener {
    boolean addMedicineBtnClicked = false;
    boolean scanMedicineBtnClicked = false;

    public TuioPanelComponent()
    {
        JButton addBtn = new JButton("Add New Medicine");
        addBtn.setVisible(true);
        addBtn.setSize(100,100);
        addBtn.addActionListener(e -> addMedicineBtnClicked = true);

        JButton scanBtn = new JButton("Scan Medicine");
        scanBtn.setVisible(true);
        scanBtn.setSize(100,100);
        scanBtn.addActionListener(e -> scanMedicineBtnClicked = true);

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(scanBtn);
        panel.setVisible(true);
        panel.setSize(600,600);

        this.add(panel);
    }
    @Override
    public void addTuioObject(TuioObject tuioObject) {
        if(addMedicineBtnClicked)
        {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setSize(600,600);


            JLabel medicineLabel = new JLabel("Enter Medicine Name");
            medicineLabel.setBounds(50,20,180,25);
            jPanel.add(medicineLabel);

            JTextField medicineNameField = new JTextField(20);
            medicineNameField.setBounds(300,20,165,25);
            jPanel.add(medicineNameField);

            JLabel doseNumberLabel = new JLabel("Enter The number of daily doses");
            doseNumberLabel.setBounds(50, 120, 250, 25);
            jPanel.add(doseNumberLabel);

            JTextField doseNumberField = new JTextField();
            doseNumberField.setBounds(300, 120, 165,25);
            jPanel.add(doseNumberField);

            JButton submit = new JButton("Submit");
            submit.setBounds(300,200, 165,50);
            submit.addActionListener(e -> {
                System.out.println("clicked the marker " + tuioObject.getSymbolID());
                System.out.println("Medicine name "+medicineNameField.getText());
                System.out.println("Dose number "+doseNumberField.getText());
            });
            jPanel.add(submit);

            JFrame addFrame = new JFrame("add new medicine");
            addFrame.setSize(600,600);
            addFrame.add(jPanel);
            addFrame.setVisible(true);
            System.out.println(" add new medicine entered ");
            addMedicineBtnClicked = false;

        }else if(scanMedicineBtnClicked)
        {
            JFrame scanFrame = new JFrame("Scan medicine");
            JPanel jPanel = new JPanel();
            jPanel.setSize(600,600);
            jPanel.setLayout(null);

            JLabel takenDose = new JLabel();
            takenDose.setText("Taken Doses : "+ 2);
            takenDose.setBounds(50,20,180,25);
            jPanel.add(takenDose);

            JLabel remainingDoses = new JLabel();
            remainingDoses.setText("Remaining Doses: " + 1);
            remainingDoses.setBounds(350,20,180,25);
            jPanel.add(remainingDoses);

            JLabel lastTakenDose = new JLabel();
            lastTakenDose.setText("Last Taken Dose Time: "+ "6:12:4");
            lastTakenDose.setBounds(50,120,300,25);
            jPanel.add(lastTakenDose);

            JButton takeDose = new JButton("Take A Dose");
            takeDose.setBounds(50,300,150,25);
            takeDose.addActionListener( e -> {
                System.out.println("Take a Dose Cliked");
            });
            jPanel.add(takeDose);

            JButton report = new JButton("Generate A Medical Report");
            report.setBounds(300,300,250,25);
            report.addActionListener( e -> {
                System.out.println("Report Clicked");
            });
            jPanel.add(report);

            scanFrame.setSize(600,600);
            scanFrame.add(jPanel);
            scanFrame.setVisible(true);
            System.out.println("scan entered ");

            scanMedicineBtnClicked = false;
        }

    }

    @Override
    public void updateTuioObject(TuioObject tuioObject) {

    }

    @Override
    public void removeTuioObject(TuioObject tuioObject) {

    }

    @Override
    public void addTuioCursor(TuioCursor tuioCursor) {

    }

    @Override
    public void updateTuioCursor(TuioCursor tuioCursor) {

    }

    @Override
    public void removeTuioCursor(TuioCursor tuioCursor) {

    }

    @Override
    public void addTuioBlob(TuioBlob tuioBlob) {

    }

    @Override
    public void updateTuioBlob(TuioBlob tuioBlob) {

    }

    @Override
    public void removeTuioBlob(TuioBlob tuioBlob) {

    }

    @Override
    public void refresh(TuioTime tuioTime) {

    }
}
