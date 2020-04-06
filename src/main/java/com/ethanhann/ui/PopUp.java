package com.ethanhann.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Window;

public class PopUp
{

    /**
     * Creates a pop-up for displaying information to the user.
     * @param title the window title
     * @param message the window message
     * @param owner the owner (parent) of the window
     * @param type the type of pop up the create; valid options are
     *             <ul>
     *              <li>{@code OK} for a pop up with only an OK button.</li>
     *              <li>{@code OK_CANCEL} for a pop up with an OK and Cancel button.</li>
     *             </ul>
     * @return Dialog a dialog object with result type of ButtonType
     */
    public static Dialog<ButtonType> createPopUp(String title, String message, Window owner, String type)
    {
        Dialog<ButtonType> dialog = new Dialog<>();

        switch (type)
        {
            case "OK":
            {
                ButtonType okButtonType = ButtonType.OK;
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

                dialog.setTitle(title);
                dialog.setContentText(message);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(owner);
                break;
            }
            case "OK_CANCEL":
            {
                ButtonType okButtonType = ButtonType.OK;
                ButtonType cancelButtonType = ButtonType.CANCEL;
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

                dialog.setTitle(title);
                dialog.setContentText(message);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(owner);
                break;
            }
            default:
            {
                ButtonType okButtonType = ButtonType.OK;
                ButtonType cancelButtonType = ButtonType.CANCEL;
                dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

                dialog.setTitle(title);
                dialog.setContentText(message);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(owner);
            }
        }


        return dialog;
    }
}
