import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert;

public class TicTacToe extends Application {

    private Pole[][] plansza = new Pole[3][3];
    private Pane root = new Pane();
    private List<Zwyciestwo> wygrana = new ArrayList<>();
    private boolean aktywna = true; //gra aktywna
    private boolean turnX = true; //daj X

    private Parent createContent() {
        root.setPrefSize(300, 300);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Pole pole = new Pole();
                pole.setTranslateX(j * 100);
                pole.setTranslateY(i * 100);
                root.getChildren().add(pole);
                plansza[j][i] = pole;
            }
        }
//metody wygranej
        for (int y = 0; y < 3; y++) {
            wygrana.add(new Zwyciestwo(plansza[0][y], plansza[1][y], plansza[2][y]));
        } //poziomo

        for (int x = 0; x < 3; x++) {
            wygrana.add(new Zwyciestwo(plansza[x][0], plansza[x][1], plansza[x][2]));
        } //pionowo

        wygrana.add(new Zwyciestwo(plansza[0][0], plansza[1][1], plansza[2][2]));
        wygrana.add(new Zwyciestwo(plansza[2][0], plansza[1][1], plansza[0][2]));
            //ukośnie
        return root;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.setTitle("MyGame");
        primaryStage.show();
    }

    private void checkState() {
        for (Zwyciestwo zwyciestwo : wygrana) {
            if (zwyciestwo.isComplete()) {
                aktywna = false;
                playWinAnimation(zwyciestwo);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Zwycięstwo!!");
                    alert.setHeaderText("Zwycięstwo, czy chcesz zagrać jeszcze raz?");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Wciśnięto OK");
                            newGame();
                        } else if (rs == ButtonType.CANCEL) {
                            System.out.println("Koniec gry");
                            System.exit(0);
                        }
                    });
            }
        }
    }

    private void playWinAnimation(Zwyciestwo zwyciestwo) {

        Line line = new Line();
        line.setStroke(Color.RED);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(10);
        line.setStartX(zwyciestwo.wygrana[0].getCenterX());
        line.setStartY(zwyciestwo.wygrana[0].getCenterY());
        line.setEndX(zwyciestwo.wygrana[0].getCenterX());
        line.setEndY(zwyciestwo.wygrana[0].getCenterY());
        root.getChildren().add(line);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new KeyValue(line.endXProperty(), zwyciestwo.wygrana[2].getCenterX()),
                new KeyValue(line.endYProperty(), zwyciestwo.wygrana[2].getCenterY())));

        timeline.play();
//rysowanie lini na wygranej pozycji

        root.getChildren().remove(line); //usuwanie lini na rzecz nowej gry

    }

     private void newGame() {
         for(int i = 0; i < 3; i++)
         {
             for(int j = 0; j < 3; j++)
             {
                 plansza[i][j].text.setText(null);
             }
         }

        turnX = true;
         aktywna = true;
    }



    private class Zwyciestwo {
        private Pole[] wygrana;
        public Zwyciestwo(Pole... wygrana) {
            this.wygrana = wygrana;
        }
        public boolean isComplete() {
            if (wygrana[0].getValue().isEmpty())
                return false;

            return wygrana[0].getValue().equals(wygrana[1].getValue())
                    && wygrana[0].getValue().equals(wygrana[2].getValue());
        }
    }
    private class Pole extends StackPane {
        private Text text = new Text();

        public Pole() {
            Rectangle border = new Rectangle(100, 100);
            border.setFill(null);
            border.setStroke(Color.BLACK);
            text.setFont(Font.font(72));
            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text);

            setOnMouseClicked(event -> {
                if (!aktywna)
                    return;

                if (event.getButton() == MouseButton.PRIMARY) {
                    if (!turnX)
                        return;

                    drawX();
                    turnX = false;
                    checkState();
                }
                else if (event.getButton() == MouseButton.SECONDARY) {
                    if (turnX)
                        return;

                    drawO();
                    turnX = true;
                    checkState();
                }
            });
        }
        public double getCenterX() {
            return getTranslateX() + 50;
        }
        public double getCenterY() {
            return getTranslateY() + 50;
        }
        public String getValue() {
            return text.getText();
        }
        private void drawX() {
            text.setText("X");
        }
        private void drawO() {
            text.setText("O");
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
