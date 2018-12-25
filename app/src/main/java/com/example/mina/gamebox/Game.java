package com.example.mina.gamebox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import static java.lang.Thread.sleep;

public class Game {

    private SecureRandom random;
    private Context context;
    private ViewGroup.LayoutParams cardParams;
    private ConstraintLayout constraintLayout;

    private Stack<Card> deck , hand , storeForDelete;
    private Pair<Float , Float> deckPosition , handPosition;

    private ArrayList<Pair<Float , Float>> suitsPosition , playAreaPosition;
    //list of stacks of all 4 suits
    private ArrayList<Stack<Card>> suitsCard ;
    private ArrayList<ArrayList<Card>> playArea;
    //holds index value of which suit stack in suitsCard
    private HashMap<String , Integer> suitIdx;
    //private ArrayList<Card> allCards ;
    private ArrayList<ArrayList<Card>> allCards;
    private int coverCardID , emptyDeckID;
    private ArrayList<String> cardType;

    //undo stack of pair( operation, pair(card, index) )
    private Stack<Pair<Integer, Pair<Card, Integer>>> undoStack;

    //const int values to hold operation number
    private static final int deckToHand = 0, handToSuits = 1, handToPlay = 2,
            playToSuits = 3, suitsToPlay = 4, playToPlay = 5,
            flipCard = 6 , handToDeck = 7;


    public Game(Context context , ConstraintLayout constraintLayout)
    {
        this.constraintLayout = constraintLayout;
        this.context = context;

        initializeGame();
    }

    public void initializeGame() {

        initializeRandom();
        initializeCardLayoutParams();
        initializeDeck();
        initializeHand();
        initializeSuits();
        initializePlayArea();
        initializeAllCards();
        distributeCards();
        initializeEmptySuitCell();
    }

    private void initializeEmptySuitCell() {

        int emptySpades = context.getResources().getIdentifier("spadesbg" , "drawable" ,  context.getPackageName());
        int emptyHearts = context.getResources().getIdentifier("heartsbg" , "drawable" ,  context.getPackageName());
        int emptyClubs = context.getResources().getIdentifier("clubsbg" , "drawable" ,  context.getPackageName());
        int emptyDiamonds = context.getResources().getIdentifier("diamondsbg" , "drawable" ,  context.getPackageName());

        Card emptySpadesCard = new Card(context , emptySpades , coverCardID , cardParams , null , constraintLayout);
        Card emptyHeartsCard = new Card(context , emptyHearts , coverCardID , cardParams , null, constraintLayout);
        Card emptyClubsCard = new Card(context , emptyClubs , coverCardID , cardParams , null, constraintLayout);
        Card emptyDiamondsCard = new Card(context , emptyDiamonds , coverCardID , cardParams , null , constraintLayout);

        emptySpadesCard.setPosition(suitsPosition.get(0));
        emptyHeartsCard.setPosition(suitsPosition.get(1));
        emptyClubsCard.setPosition(suitsPosition.get(2));
        emptyDiamondsCard.setPosition(suitsPosition.get(3));

        constraintLayout.addView(emptySpadesCard);
        constraintLayout.addView(emptyHeartsCard);
        constraintLayout.addView(emptyClubsCard);
        constraintLayout.addView(emptyDiamondsCard);
    }

    private void initializeRandom() {
        random = new SecureRandom();
    }

    private void distributeCards() {
        fillPlayAndDeck();
        //fillPlayArea();
        //fillDeck();
    }

    private void fillPlayAndDeck() {
        Card card = new Card(context , emptyDeckID , coverCardID , cardParams , onTouchListener , constraintLayout);
        card.toDeck(deckPosition);
        card.showCard();
        deck.add(card);
        addCardToConstraint(card);

        for(int i = 13 ; i >= 1 ; i--){
            for(int j = 0 ; j < 4 ; j++) {

                boolean ok = false;
                while(!ok){

                    int idx = random.nextInt(14);

                    if(idx >= 7 && deck.size() < 25 ){
                        card = allCards.get(i-1).get(j);
                        card.toDeck(deckPosition);
                        deck.add(card);
                        ok = true;
                    }
                    else if(idx < 7){

                        if(playArea.get(idx).size() == idx + 1) continue;

                        card = allCards.get(i-1).get(j);

                        if (playArea.get(idx).size() < idx) {
                            card.hideCard();
                            card.setFaceUp(false);
                        }

                        card.toPlayArea(idx, playArea.get(idx).size(), new Pair<Float, Float>(playAreaPosition.get(idx).first,
                                playAreaPosition.get(idx).second + card.getLayoutParams().height / 4 * playArea.get(idx).size()));
                        card.setLastPosition(card.getPosition());
                        playArea.get(idx).add(card);

                        ok = true;
                    }
                }

                addCardToConstraint(card);
            }
        }
    }

    private void addCardToConstraint(Card card) {
        card.reAddToConstraint();
        /*
        constraintLayout.removeView(card);
        constraintLayout.addView(card);
        */
    }

    private void initializeAllCards() {
        undoStack = new Stack<>();
        allCards = new ArrayList<ArrayList<Card>>();
        storeForDelete = new Stack<Card>();
        cardType = new ArrayList<String>();
        suitIdx = new HashMap<String, Integer>();
        cardType.add("spades");
        cardType.add("hearts");
        cardType.add("clubs");
        cardType.add("diamonds");
        coverCardID = context.getResources().getIdentifier("cardcover" , "drawable" ,  context.getPackageName());
        emptyDeckID = context.getResources().getIdentifier("empty" , "drawable" ,  context.getPackageName());

        for(int j = 1 ; j <= 13 ; j++){
            allCards.add(new ArrayList<Card>());
            for(int i = 0 ; i < 4 ; i++){
                String name = cardType.get(i) + Integer.toString(j);
                int id = context.getResources().getIdentifier(name , "drawable" ,  context.getPackageName());

                Card card = new Card(context , id , coverCardID , cardParams , onTouchListener , constraintLayout );
                card.setPosition(deckPosition);
                allCards.get(j-1).add(card);
                storeForDelete.add(card);
                suitIdx.put(cardType.get(i) , i);
            }
        }

    }

    private void initializeCardLayoutParams() {
        cardParams = constraintLayout.findViewById(R.id.deckCard).getLayoutParams();
    }

    private void initializePlayArea() {
        playAreaPosition = new ArrayList<Pair<Float,Float>>(7);
        playArea = new ArrayList<ArrayList<Card>>(7);

        for(int i = 0 ; i < 7 ; i++){
            playArea.add(new ArrayList<Card>());
            String name = "playArea" + Integer.toString(i);

            int id = context.getResources().getIdentifier(name , "id" , context.getPackageName());
            playAreaPosition.add(new Pair<Float, Float>(constraintLayout.findViewById(id).getX() ,
                    constraintLayout.findViewById(id).getY()));
        }
    }

    private void initializeSuits() {
        suitsPosition = new ArrayList<Pair<Float, Float>>();
        suitsCard = new ArrayList<Stack<Card>>(4);

        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitSpades).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitHearts).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitClubs).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitDiamonds).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));

        for(int i = 0 ; i < 4 ; i++)
            suitsCard.add(new Stack<Card>());
    }

    private void initializeHand() {
        hand = new Stack<Card>();
        handPosition = new Pair<Float , Float>(constraintLayout.findViewById(R.id.handCard).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY());
    }

    private void initializeDeck() {
        deck = new Stack<Card>();
        deckPosition = new Pair<Float , Float>(constraintLayout.findViewById(R.id.deckCard).getX() ,
                constraintLayout.findViewById(R.id.deckCard).getY());
    }

    private Boolean checkForPlayMotion(int idx , int innerIdx){
        Boolean isOk = true;
        for(int i = innerIdx + 1 ; i < playArea.get(idx).size() ; i++){
            if(playArea.get(idx).get(i).getNumber() + 1 != playArea.get(idx).get(i-1).getNumber())
                isOk = false;
        }
        return  isOk && playArea.get(idx).get(innerIdx).getFaceUp();
    }

    private Boolean canLandToPlayArea(Card dragged , Card target){
        return  target.getRed() != dragged.getRed() && target.getNumber() == dragged.getNumber() + 1;
    }

    private Boolean canLandToSuits(Card dragged , Card target){
        return  target.getRed() == dragged.getRed() && target.getNumber() + 1 == dragged.getNumber();
    }

    public boolean isDragging(float lastX , float lastY, float currX , float currY){
        float deltaX = Math.abs(lastX-currX);
        float deltaY = Math.abs(lastY-currY);

         float acceptedDelta = cardParams.height/2;

        return deltaY > acceptedDelta || deltaX > acceptedDelta;
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            Card card = ((Card) view);
            float elevation = 1;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    if (card.getPlay()) {
                            if (checkForPlayMotion(card.getPlayIdx(), card.getInPlayIdx())) {
                                int mainCardIdx = card.getInPlayIdx();
                                for (int i = card.getInPlayIdx(); i < playArea.get(card.getPlayIdx()).size(); i++) {
                                    card = playArea.get(card.getPlayIdx()).get(i);
                                    card.setPosition(event.getRawX() - card.getLayoutParams().width / 2,
                                            event.getRawY() - card.getLayoutParams().height / 2 + card.getLayoutParams().height / 4 * (i - mainCardIdx));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        card.setElevation(elevation++);
                                    }
                                }
                            }
                        } else if (card.getFinished() || card.getHand()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                card.setElevation(elevation);
                            }
                            card.setPosition(event.getRawX() - card.getLayoutParams().width / 2,
                                    event.getRawY() - card.getLayoutParams().height / 2);
                        }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    //Click On card from hand or play tio suits
                    if (!isDragging(card.getLastPosition().first , card.getLastPosition().second , card.getPosition().first , card.getPosition().second)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            card.setElevation(0f);
                        }

                        card.setPosition(card.getLastPosition());

                        if (card.getHand()) {
                            int lastCardNumber = 0;
                            if (!suitsCard.get(suitIdx.get(card.getName())).empty()) {
                                lastCardNumber = suitsCard.get(suitIdx.get(card.getName())).peek().getNumber();
                            }
                            if (card.getNumber() == lastCardNumber + 1) {
                                cardToSuits(suitIdx.get(card.getName()), card);
                            }
                        } else if (card.getDeck()) {

                            if (deck.size() == 1) {
                                reFillDeck();
                            } else {
                                cardToHand(card);
                            }
                        }
                        else if(card.getPlay()){
                            if(card.getInPlayIdx() == playArea.get(card.getPlayIdx()).size() - 1){
                                int lastCardNumber = 0;
                                if (!suitsCard.get(suitIdx.get(card.getName())).empty()) {
                                    lastCardNumber = suitsCard.get(suitIdx.get(card.getName())).peek().getNumber();
                                }
                                if (card.getNumber() == lastCardNumber + 1) {
                                    cardToSuits(suitIdx.get(card.getName()), card);
                                }
                            }
                        }
                    }
                    else //check if card is dragged and landed on play or suit
                    {
                        //check landing on play
                        int playIdx = -1 , suitsIdx = -1;
                        for(int i = 0 ; i < 7 ; i++){
                            if(playArea.get(i).isEmpty()){
                                if(checkIfInside(event.getRawX() , event.getRawY() , playAreaPosition.get(i).first ,
                                        playAreaPosition.get(i).first + cardParams.width , playAreaPosition.get(i).second
                                        , playAreaPosition.get(i).second + cardParams.height )){
                                    playIdx = i;
                                    break;
                                }
                            }
                            else {
                                if(canLandToPlayArea(card , playArea.get(i).get(playArea.get(i).size()-1)) && checkIfInside(event.getRawX() ,
                                        event.getRawY() , playAreaPosition.get(i).first ,
                                        playAreaPosition.get(i).first + cardParams.width, playAreaPosition.get(i).second
                                        , playAreaPosition.get(i).second + cardParams.height + (playArea.get(i).size() - 1) * cardParams.height / 4)){
                                    playIdx = i;
                                    break;
                                }
                            }
                        }

                        //check landing on suit
                        if(!card.getPlay() || card.getInPlayIdx() == playArea.get(card.getPlayIdx()).size()-1){
                            if(suitsCard.get(suitIdx.get(card.getName())).isEmpty() ){
                                if (checkIfInside(event.getRawX() ,
                                        event.getRawY() , suitsPosition.get(suitIdx.get(card.getName())).first ,
                                        suitsPosition.get(suitIdx.get(card.getName())).first + cardParams.width ,
                                        suitsPosition.get(suitIdx.get(card.getName())).second ,
                                        suitsPosition.get(suitIdx.get(card.getName())).second + cardParams.height) && card.getNumber() == 1) {
                                    suitsIdx = suitIdx.get(card.getName());
                                }
                            }
                            else{
                                if(canLandToSuits(card , suitsCard.get(suitIdx.get(card.getName())).peek() ) && checkIfInside(event.getRawX() ,
                                        event.getRawY() , suitsPosition.get(suitIdx.get(card.getName())).first ,
                                        suitsPosition.get(suitIdx.get(card.getName())).first + cardParams.width ,
                                        suitsPosition.get(suitIdx.get(card.getName())).second ,
                                        suitsPosition.get(suitIdx.get(card.getName())).second + cardParams.height) ){
                                    suitsIdx = suitIdx.get(card.getName());
                                }
                            }
                        }

                        //check if landed successfully
                        if (playIdx == -1 && suitsIdx == -1){
                            if(card.getPlay()){
                                for(int i = card.getInPlayIdx() ; i < playArea.get(card.getPlayIdx()).size() ; i++){
                                    card = playArea.get(card.getPlayIdx()).get(i);
                                    card.setPosition(card.getLastPosition());
                                }
                            }
                            else { //back to normal position if not correct land
                                card.setPosition(card.getLastPosition());
                            }
                        }
                        else if(playIdx != -1){ //to play Area (undo play/hand/suit to play)
                            cardToPlay(playIdx , card , false);
                        }
                        else { //to suit (undo hand/play to suit)
                            cardToSuits(suitsIdx , card);
                        }
                    }
                    break;
                }
            }
            return true;
        }
    };

    private boolean checkIfInside(float touchX , float touchY , float startX , float endX , float startY , float endY) {
        return (touchX >= startX && touchX <= endX && touchY >= startY && touchY <= endY);
    }

    private void reFillDeck(){
        undoStack.push(new Pair<Integer, Pair<Card, Integer>> (handToDeck, new Pair<Card, Integer> (hand.peek(), -1)));
        while(!hand.empty()){
            Card card = hand.peek();
            hand.pop();
            card.toDeck(deckPosition);
            deck.push(card);
        }
    }

    private void cardToHand(Card card){
        undoStack.push(new Pair<Integer, Pair<Card, Integer>> (deckToHand, new Pair<Card, Integer> (card, -1)));
        deck.pop();
        card.toHand(handPosition);
        hand.push(card);
    }

    private void cardToPlay(int targetIdx , Card card , boolean fromUndo) {

        int currIdx = card.getPlayIdx();
        int cardInPlayIdx = card.getInPlayIdx();
        Boolean isPlay = card.getPlay();
        Boolean isFinish = card.getFinished();
        Boolean isHand = card.getHand();

        if(playArea.get(targetIdx).size() == 0 && card.getNumber() != 13 && !fromUndo){
            if(isPlay) {
                for (int i = cardInPlayIdx; i < playArea.get(currIdx).size(); i++) {
                    card = playArea.get(currIdx).get(i);
                    card.setPosition(card.getLastPosition());
                }
            }
            else if(isHand || isFinish) {
                card.setPosition(card.getLastPosition());
            }

            return;
        }

        if(isPlay){ //play to play
            undoStack.push(new Pair<Integer, Pair<Card, Integer>>(playToPlay, new Pair<Card, Integer>(card,currIdx)));
            for(int i = cardInPlayIdx ; i < playArea.get(currIdx).size() ; i++){
                card = playArea.get(currIdx).get(i);
                card.toPlayArea(targetIdx , playArea.get(targetIdx).size() , new Pair<Float, Float>(playAreaPosition.get(targetIdx).first ,
                        playAreaPosition.get(targetIdx).second + playArea.get(targetIdx).size() * cardParams.height / 4));
                playArea.get(targetIdx).add(card);
            }
            for(int i = playArea.get(currIdx).size() - 1 ; i >= cardInPlayIdx ; i--){
                playArea.get(currIdx).remove(i);
            }
            if(!playArea.get(currIdx).isEmpty()){
                if(!playArea.get(currIdx).get(playArea.get(currIdx).size()-1).getFaceUp()){
                    playArea.get(currIdx).get(playArea.get(currIdx).size()-1).showCard();
                    Pair<Integer , Pair<Card , Integer> > temp = undoStack.peek();
                    undoStack.pop();
                    undoStack.push(new Pair<Integer, Pair<Card, Integer>>(flipCard, new Pair<Card, Integer>(
                            playArea.get(currIdx).get(playArea.get(currIdx).size()-1), -1)));
                    undoStack.push(temp);
                }
            }
        }
        else if(isHand){ //hand to play
            undoStack.push(new Pair<Integer, Pair<Card, Integer>>(handToPlay, new Pair<Card, Integer>(card, -1)));
            card.toPlayArea(targetIdx , playArea.get(targetIdx).size() , new Pair<Float, Float>(playAreaPosition.get(targetIdx).first ,
                    playAreaPosition.get(targetIdx).second + playArea.get(targetIdx).size() * cardParams.height / 4));
            playArea.get(targetIdx).add(card);
            hand.pop();
        }
        else if(isFinish){ //suits to play
            undoStack.push(new Pair<Integer, Pair<Card, Integer>>(suitsToPlay, new Pair<Card, Integer>(card, -1)));
            card.toPlayArea(targetIdx , playArea.get(targetIdx).size() , new Pair<Float, Float>(playAreaPosition.get(targetIdx).first ,
                    playAreaPosition.get(targetIdx).second + playArea.get(targetIdx).size() * cardParams.height / 4));
            playArea.get(targetIdx).add(card);
            suitsCard.get(suitIdx.get(card.getName())).pop();
        }
    }

    private void cardToSuits(int idx  , Card card){
        if(card.getPlay()){
            playArea.get(card.getPlayIdx()).remove(card.getInPlayIdx());
            if(!playArea.get(card.getPlayIdx()).isEmpty() && !playArea.get(card.getPlayIdx()).get(playArea.get(card.getPlayIdx()).size() - 1).getFaceUp()){
                playArea.get(card.getPlayIdx()).get(playArea.get(card.getPlayIdx()).size() - 1).showCard();
                undoStack.push(new Pair<>(flipCard, new Pair<>(playArea.get(card.getPlayIdx()).get(playArea.get(card.getPlayIdx()).size() - 1) , -1)));
            }

            undoStack.push(new Pair<>(playToSuits, new Pair<>(card, card.getPlayIdx())));
        }
        else if(card.getHand()){
            undoStack.push(new Pair<>(handToSuits, new Pair<>(card, -1)));
            hand.pop();
        }
        card.toSuits(idx , suitsPosition.get(idx));
        suitsCard.get(idx).add(card);
    }

    public void disposeGame()
    {
        while(!storeForDelete.isEmpty()){
            constraintLayout.removeView(storeForDelete.peek());
            storeForDelete.pop();
        }

        while (!undoStack.empty())
            undoStack.pop();
    }

    public void undo()
    {
        if(undoStack.empty()) return;

        Integer operation = undoStack.peek().first;
        Card card = undoStack.peek().second.first;
        Integer playAreaIdx = undoStack.peek().second.second;

        undoStack.pop();

        if(!undoStack.empty() && undoStack.peek().first == flipCard) undo();
        Log.i("tag", card.getName() + Integer.toString(card.getNumber()) + " " + Integer.toString(operation)) ;
        switch(operation)
        {
            case deckToHand: //move card from hand to deck
                card.toDeck(deckPosition);
                deck.push(card);
                hand.pop();
                break;
            case handToSuits: //move card from suits to hand
                hand.push(card);
                suitsCard.get(suitIdx.get(card.getName())).pop();
                card.toHand(handPosition);
                break;
            case playToSuits: //move card from suits to play area
                suitsCard.get(suitIdx.get(card.getName())).pop();
                card.toPlayArea(playAreaIdx, playArea.get(playAreaIdx).size(), new Pair<>(playAreaPosition.get(playAreaIdx).first,
                        playAreaPosition.get(playAreaIdx).second + card.getLayoutParams().height / 4 * playArea.get(playAreaIdx).size()));
                playArea.get(playAreaIdx).add(card);
                break;
            case suitsToPlay: //move card from play area to suits
                cardToSuits(suitIdx.get(card.getName()), card);
                undoStack.pop();
                if(!undoStack.empty() && undoStack.peek().first == flipCard) undoStack.pop();
                break;
            case handToPlay: //move card from play area to hand
                hand.push(card);
                //remove card from play *** here ***
                playArea.get(card.getPlayIdx()).remove(playArea.get(card.getPlayIdx()).size()-1);
                card.toHand(handPosition);
                break;
            case playToPlay: //move card from play area to play area
                cardToPlay(playAreaIdx, card , true);
                undoStack.pop();
                if(!undoStack.empty() && undoStack.peek().first == flipCard) undo();
                break;
            case handToDeck: //move cards from deck to hand (undo refill)
               while(deck.size() > 1)
               {
                   Card deckTop = deck.peek();
                   deckTop.toHand(handPosition);
                   deck.pop();
                   hand.push(deckTop);
               }
                break;
            case flipCard: //flips card down
                card.hideCard();
                break;
        }
    }

    @SuppressLint("NewApi")
    public void hint() {

        Card card1,card2;
        Toast toast= new Toast(context);
        boolean moves = false;

        for (int i = 0; i <= 12; i++) {
            for (int x = 0; x < (allCards.get(i).size()); x++) {
                if (i < 12) {
                    for (int y = 0; y < (allCards.get(i + 1).size()); y++) {
                        if (allCards.get(i).get(x).getRed() != allCards.get(i + 1).get(y).getRed()) {
                            card1 = allCards.get(i).get(x);
                            card2 = allCards.get(i + 1).get(y);

                            if ((playArea.get(card1.getPlayIdx()).size() - 1 == card2.getInPlayIdx())) {

                                moves = true;
                            }
                        }
                    }
                }
            }
        }
        if (moves == false) {

            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.makeText(context ,"Game Over!", Toast.LENGTH_LONG).show();
        }
    }
}