package gui;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class SceneUtil {

    public static Scene create(Parent root, double w, double h) {
        Scene s = new Scene(root, w, h);
        s.getStylesheets().add(SceneUtil.class.getResource("/gui/style.css").toExternalForm());
        return s;
    }
}
