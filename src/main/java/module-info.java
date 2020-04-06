module OSS.main
{
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires sqlite.jdbc;

    opens com.ethanhann.ui.login to javafx.fxml;
    opens com.ethanhann.ui.signup to javafx.fxml;
    opens com.ethanhann.ui.shopping_cart to javafx.fxml;
    opens com.ethanhann.ui.account to javafx.fxml;
    opens com.ethanhann.ui.home to javafx.fxml;
    opens com.ethanhann.ui.orders to javafx.fxml;

    exports com.ethanhann.ui.home;
    exports com.ethanhann.ui.signup;
    exports com.ethanhann.ui.shopping_cart;
    exports com.ethanhann.ui.account;
    exports com.ethanhann.ui.orders;
    exports com.ethanhann.ui.login;
    exports com.ethanhann.backend;
    exports com.ethanhann.users;
    exports com.ethanhann.validation;
}