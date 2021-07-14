
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import TUIO.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TuioPanelComponent extends JComponent implements TuioListener {

    boolean addMedicineBtnClicked = false;
    boolean scanMedicineBtnClicked = false;
    private SQLDatabaseConnection db;

    public TuioPanelComponent() throws SQLException, ClassNotFoundException {
        db = new SQLDatabaseConnection();
        JButton addBtn = new JButton("Add New Medicine");
        addBtn.setVisible(true);
        addBtn.setSize(100, 100);
        addBtn.addActionListener(e -> {
            addMedicineBtnClicked = true;
            scanMedicineBtnClicked = false;
        });

        JButton scanBtn = new JButton("Scan Medicine");
        scanBtn.setVisible(true);
        scanBtn.setSize(100, 100);
        scanBtn.addActionListener(e -> {
            scanMedicineBtnClicked = true;
            addMedicineBtnClicked = false;
        });

        JPanel panel = new JPanel();
        panel.add(addBtn);
        panel.add(scanBtn);
        panel.setVisible(true);
        panel.setSize(600, 600);

        this.add(panel);
    }

    @Override
    public void addTuioObject(TuioObject tuioObject) {
        if (addMedicineBtnClicked) {
            try {
                if (db.medicineExist(tuioObject.getSymbolID())) {
                    JOptionPane.showMessageDialog(null, "Error this medicine already Exists ", "Error", JOptionPane.WARNING_MESSAGE);

                } else {
                    JPanel jPanel = new JPanel();
                    jPanel.setLayout(null);
                    jPanel.setSize(600, 600);

                    JLabel medicineLabel = new JLabel("Enter Medicine Name");
                    medicineLabel.setBounds(50, 20, 180, 25);
                    jPanel.add(medicineLabel);

                    JTextField medicineNameField = new JTextField(20);
                    medicineNameField.setBounds(300, 20, 165, 25);
                    jPanel.add(medicineNameField);

                    JLabel doseNumberLabel = new JLabel("Enter The number of daily doses");
                    doseNumberLabel.setBounds(50, 120, 250, 25);
                    jPanel.add(doseNumberLabel);

                    JTextField doseNumberField = new JTextField();
                    doseNumberField.setBounds(300, 120, 165, 25);
                    jPanel.add(doseNumberField);

                    JButton submit = new JButton("Submit");
                    submit.setBounds(300, 200, 165, 50);
                    submit.addActionListener(e -> {
                        if (db.Submit_medicine(tuioObject.getSymbolID(), medicineNameField.getText(), Integer.valueOf(doseNumberField.getText()))) {
                            JOptionPane.showMessageDialog(this, "Successfully Add The New medicine", "Added new medicine", JOptionPane.WARNING_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Error exist ", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                        System.out.println("clicked the marker " + tuioObject.getSymbolID());
                        System.out.println("Medicine name " + medicineNameField.getText());
                        System.out.println("Dose number " + doseNumberField.getText());
                    });
                    jPanel.add(submit);

                    JFrame addFrame = new JFrame("add new medicine");
                    addFrame.setSize(600, 600);
                    addFrame.add(jPanel);
                    addFrame.setVisible(true);
                    System.out.println(" add new medicine entered ");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TuioPanelComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            addMedicineBtnClicked = false;

        } else if (scanMedicineBtnClicked) {
            JFrame scanFrame = new JFrame("Scan medicine");
            JPanel jPanel = new JPanel();
            jPanel.setSize(600, 600);
            jPanel.setLayout(null);

            JLabel medicineName = null;
            try {
                medicineName = new JLabel(db.medicineName(tuioObject.getSymbolID()));
            } catch (SQLException ex) {
                Logger.getLogger(TuioPanelComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            medicineName.setBounds(50, 40, 300, 25);
            jPanel.add(medicineName);

            //  medicineName.setBounds(350, 140, 300, 25);
            JLabel takenDose = new JLabel();
            takenDose.setText("Taken Doses : " + db.taken_doses(tuioObject.getSymbolID()));
            takenDose.setBounds(50, 140, 180, 25);
            jPanel.add(takenDose);

            JLabel remainingDoses = new JLabel();
            remainingDoses.setText("Remaining Doses: " + db.remainig_doses(tuioObject.getSymbolID()));
            remainingDoses.setBounds(350, 140, 180, 25);
            jPanel.add(remainingDoses);

            JLabel lastTakenDose = new JLabel();
            try {
                lastTakenDose.setText("Last Taken Dose Time: " + db.getLastDoseTime(tuioObject.getSymbolID()));
            } catch (SQLException ex) {
                Logger.getLogger(TuioPanelComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
            lastTakenDose.setBounds(350, 40, 300, 25);
            jPanel.add(lastTakenDose);

            JButton takeDose = new JButton("Take A Dose");
            takeDose.setBounds(50, 300, 150, 25);
            takeDose.addActionListener((ActionEvent e) -> {
                if (db.take_pill(tuioObject.getSymbolID())) {
                    JOptionPane.showMessageDialog(null, "You Have taken your subscribe dose", "Dose taken", JOptionPane.WARNING_MESSAGE);
                    takenDose.setText("Taken Doses : " + db.taken_doses(tuioObject.getSymbolID()));
                    remainingDoses.setText("Remaining Doses: " + db.remainig_doses(tuioObject.getSymbolID()));
                    scanFrame.repaint();
                    try {
                        lastTakenDose.setText("Last Taken Dose Time: " + db.getLastDoseTime(tuioObject.getSymbolID()));
                    } catch (SQLException ex) {
                        Logger.getLogger(TuioPanelComponent.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "You have exceeded the number of doses today!!", "You Can't take this medicine", JOptionPane.ERROR_MESSAGE);
                }
            });
            jPanel.add(takeDose);

            JButton report = new JButton("Generate A Medical Report");
            report.setBounds(300, 300, 250, 25);
            report.addActionListener(e -> {
                String[][] data = db.generate_report_all(tuioObject.getSymbolID());
                String[] colNams = {"Medicine Name ", "Date", "Time"};
                JTable table = new JTable(data, colNams);
                JScrollPane sp = new JScrollPane(table);
                sp.setSize(600, 600);
                JFrame reportFrame = new JFrame("Medical Report");
                reportFrame.add(sp);
                reportFrame.setSize(600, 600);
                reportFrame.setVisible(true);
            });
            jPanel.add(report);

            scanFrame.setSize(600, 600);
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
