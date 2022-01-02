package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDo;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

/**
 * @author : D.D.Sandaruwan <dulanjayasandaruwan1998@gmail.com>
 * @Since : 29/05/2021
 **/
public class ToDoFormController {
    public Label lblTitle;
    public Label lblId;
    public Pane paneAddNewToDo;
    public Button btnUpdate;
    public Button btnDelete;
    public TextField txtUpdateToDo;
    public AnchorPane root;
    public TextField txtNewToDO;
    public String id;
    public ListView <ToDo> lstToDos;


    public void initialize() {

        String userID = LoginFormController.userID;
        String userName = LoginFormController.name;

        lblTitle.setText("Hi "+userName+" Welcome to the To-Do List");
        lblId.setText(userID);

        paneAddNewToDo.setVisible(false);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtUpdateToDo.setDisable(true);

        loadList();

        lstToDos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDo>() {
            @Override
            public void changed(ObservableValue<? extends ToDo> observable, ToDo oldValue, ToDo newValue) {
                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
                txtUpdateToDo.setDisable(false);

                ToDo selectedItem = lstToDos.getSelectionModel().getSelectedItem();

                if(selectedItem == null){
                    return ;
                }

                txtUpdateToDo.setText(selectedItem.getDescription());
                id = selectedItem.getId();

            }
        });

    }


    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        paneAddNewToDo.setVisible(true);
        txtNewToDO.requestFocus();
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        Connection connection = DBConnection.getInstance().getConnection();

        String description = txtUpdateToDo.getText();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ?  where id = ? ; ");

            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,id);

            preparedStatement.executeUpdate();

            loadList();
            btnDelete.setDisable(true);
            btnUpdate.setDisable(true);
            txtUpdateToDo.setDisable(true);
            txtUpdateToDo.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete this todo.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonTypeOptional = alert.showAndWait();

        if(buttonTypeOptional.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            try {
               PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");

                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();

                loadList();

                btnDelete.setDisable(true);
                btnUpdate.setDisable(true);
                txtUpdateToDo.setDisable(true);
                txtUpdateToDo.clear();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnLogOut(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out", ButtonType.YES, ButtonType.NO);
        Optional <ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)) {
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) this.root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }
    }

    public void btnAddToListOnActon(ActionEvent actionEvent) {
        addToDo();
        loadList();
    }

    public String autoGenerateID()  {
        Connection connection = DBConnection.getInstance().getConnection();

        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String newID = null;

        try {
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            if(resultSet.next()){

                String oldID = resultSet.getString(1);

                oldID = oldID.substring(1, 4);

                int intId = Integer.parseInt(oldID);

                intId = intId + 1;

                if(intId < 10){
                    newID = "T00"+intId;
                }
                else if(intId < 100){
                    newID = "T0"+intId;
                }
                else {
                    newID = "T"+intId;
                }
            }
            else{
                newID = "T001";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;

    }

    public void addToDo(){

        Connection connection = DBConnection.getInstance().getConnection();

        String id = autoGenerateID();
        String description = txtNewToDO.getText();
        String userID = lblId.getText();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,userID);

            preparedStatement.executeUpdate();

            paneAddNewToDo.setVisible(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadList(){

        ObservableList<ToDo> items = lstToDos.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");

            preparedStatement.setObject(1,LoginFormController.userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDo object = new ToDo(id, description, user_id);

                items.add(object);
            }
            lstToDos.refresh();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}