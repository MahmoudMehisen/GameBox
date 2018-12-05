package com.example.mina.gamebox;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Placeholder;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Game {

    private Random random;
    private Context context;
    private ViewGroup.LayoutParams cardParams;
    private ConstraintLayout constraintLayout;

    private Stack<Card> deck , hand;
    private Pair<Float , Float> deckPosition , handPosition;

    private ArrayList<Pair<Float , Float>> suitsPosition , playAreaPosition;
    private ArrayList<Stack<Card>> suitsCard ;
    private ArrayList<ArrayList<Card>> playArea;

    private HashMap<String , Integer> suitIdx;
    private ArrayList<Card> allCards;
    private int coverCardID , emptyDeckID;
    private ArrayList<String> cardType;

    public Game(Context context , ConstraintLayout constraintLayout)
    {
        this.constraintLayout = constraintLayout;
        this.context = context;

        initializeGame();
    }

    private void initializeGame() {
        allCards = new ArrayList<Card>(52);

        initializeRandom();
        initializeCardLayoutParams();
        initializeDeck();
        initializeHand();
        initializeSuits();
        initializePlayArea();
        initializeAllCards();
        distributeCards();
    }

    private void initializeRandom() {
        random = new Random(System.currentTimeMillis());
    }

    private void distributeCards() {
        fillPlayArea();
        fillDeck();
    }

    private void fillPlayArea() {
        for(int i = 1 ; i <= 7 ; i++){
            while(playArea.get(i-1).size() < i){
                int randPos = random.nextInt(allCards.size());

                Card card = allCards.get(randPos);
                allCards.remove(randPos);

                if(playArea.get(i-1).size() < i-1){
                    card.hideCard();
                    card.setFaceUp(false);
                }

                card.toPlayArea(i-1 , playArea.get(i-1).size() , new Pair<Float, Float>(playAreaPosition.get(i-1).first ,
                        playAreaPosition.get(i-1).second + card.getLayoutParams().height / 4 * playArea.get(i-1).size()));
                playArea.get(i-1).add(card);

                addCardToConstraint(card);
            }
        }
    }

    private void addCardToConstraint(Card card) {
        constraintLayout.removeView(card);
        constraintLayout.addView(card);
    }

    private void fillDeck() {
        Card card = new Card(context , emptyDeckID , coverCardID , cardParams , onTouchListener , onDragListener , constraintLayout , onClickListener);
        card.toDeck(deckPosition);
        card.showCard();
        deck.add(card);
        addCardToConstraint(card);

        while(!allCards.isEmpty()){
            int randPos = random.nextInt(allCards.size());
            card = allCards.get(randPos);
            allCards.remove(randPos);
            card.toDeck(deckPosition);
            deck.add(card);
            addCardToConstraint(card);
        }
    }

    private void initializeAllCards() {
        allCards = new ArrayList<Card>();
        cardType = new ArrayList<String>();
        suitIdx = new HashMap<String, Integer>();
        cardType.add("spades");
        cardType.add("hearts");
        cardType.add("clubs");
        cardType.add("diamonds");
        coverCardID = context.getResources().getIdentifier("cardcover" , "drawable" ,  context.getPackageName());
        emptyDeckID = context.getResources().getIdentifier("empty" , "drawable" ,  context.getPackageName());

        for(int i = 0 ; i < 4 ; i++){
            for(int j = 1 ; j <= 13 ; j++){
                String name = cardType.get(i) + Integer.toString(j);
                int id = context.getResources().getIdentifier(name , "drawable" ,  context.getPackageName());

                Card card = new Card(context , id , coverCardID , cardParams , onTouchListener , onDragListener , constraintLayout , onClickListener);
                card.setPosition(deckPosition);

                allCards.add(card);
            }
            suitIdx.put(cardType.get(i) , i);

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

        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitDiamonds).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitHearts).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitSpades).getX() ,
                constraintLayout.findViewById(R.id.handCard).getY()));
        suitsPosition.add(new Pair<Float , Float>(constraintLayout.findViewById(R.id.suitClubs).getX() ,
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

    private Boolean canLand(Card dragged , Card target){
        return  target.getRed() != dragged.getRed() && target.getNumber() == dragged.getNumber() + 1;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Card card = ((Card) v);
            if(card.getFaceUp() && !card.getDeck()){
                int lastCardNumber = 0;
                if(!suitsCard.get(suitIdx.get(card.getName())).empty()){
                    lastCardNumber = suitsCard.get(suitIdx.get(card.getName())).peek().getNumber();
                }
                Log.i("PlayArea" , "Clicked" + " " + Integer.toString(lastCardNumber) + " " + Integer.toString(card.getNumber()));
                if(card.getNumber() == lastCardNumber + 1){
                    cardToSuits(suitIdx.get(card.getName()) , card);
                }
            }
            else if(card.getDeck()){
                if(deck.size() == 1){
                    reFillDeck();
                }
                else {
                    cardToHand(card);
                }
            }
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {

            Card card = ((Card) view);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    if (card.getFaceUp() && !card.getDeck()) {
                        int lastCardNumber = 0;
                        if (!suitsCard.get(suitIdx.get(card.getName())).empty()) {
                            lastCardNumber = suitsCard.get(suitIdx.get(card.getName())).peek().getNumber();
                        }
                        if (card.getNumber() == lastCardNumber + 1) {
                            card.setNewState("Suit");
                        }
                    } else if (card.getDeck()) {
                        if (deck.size() == 1) {
                            reFillDeck();
                        } else {
                            card.setNewState("Hand");
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (card.getPlay()) {
                        if (checkForPlayMotion(card.getPlayIdx(), card.getInPlayIdx())) {
                            int mainCardIdx = card.getInPlayIdx();
                            for (int i = card.getInPlayIdx(); i < playArea.get(card.getPlayIdx()).size(); i++) {
                                card = playArea.get(card.getPlayIdx()).get(i);
                                card.setPosition(event.getRawX() - card.getLayoutParams().width / 2,
                                        event.getRawY() - card.getLayoutParams().height / 2 + card.getLayoutParams().height / 4 * (i - mainCardIdx));
                            }
                        }
                    } else if (card.getFinished() || card.getHand()) {
                        card.setPosition(event.getRawX() - card.getLayoutParams().width / 2,
                                event.getRawY() - card.getLayoutParams().height / 2);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Log.i("PlayArea" , "outer");
                    if (card.getPosition().equals(card.getLastPosition())) {

                        switch (card.getNewState()) {
                            case "Hand":
                            {
                                cardToHand(card);
                                break;
                            }
                            case "Suit":
                            {
                                cardToSuits(suitIdx.get(card.getName()), card);
                                break;
                            }

                        }
                    }
                    else
                    {
                        Log.i("PlayArea" , "inner");

                        int playIdx = -1;
                        for(int i = 0 ; i < 7 ; i++){
                            if(playArea.get(i).isEmpty()){
                                if(checkIfInside(event.getRawX() , event.getRawY() , playAreaPosition.get(i).first ,
                                        playAreaPosition.get(i).first + cardParams.width , playAreaPosition.get(i).second
                                            , playAreaPosition.get(i).second + cardParams.height )){
                                    playIdx = i;
                                    break;
                                }
                            }
                            else{
                                //Log.i("position" , Float.toString(event.getRawX()) + " "+ Float.toString(event.getRawY()) + " "+ Float.toString(playAreaPosition.get(i).first) + " "+  + " " + )
                                Log.i("position" , Boolean.toString(canLand(card , playArea.get(i).get(playArea.get(i).size()-1)) ));
                                if(canLand(card , playArea.get(i).get(playArea.get(i).size()-1)) && checkIfInside(event.getRawX() , event.getRawY() , playAreaPosition.get(i).first ,
                                        playAreaPosition.get(i).first + cardParams.width, playAreaPosition.get(i).second + (playArea.get(i).size() - 1) * cardParams.height / 4
                                        , playAreaPosition.get(i).second + cardParams.height + (playArea.get(i).size() - 1) * cardParams.height / 4)){
                                    playIdx = i;
                                    break;
                                }
                            }
                        }

                        if (playIdx == -1){
                            if(card.getPlay()){
                                Log.i("positoin" , Integer.toString(playIdx) + " " + card.getName() + " " + Integer.toString(card.getNumber()));
                                for(int i = card.getInPlayIdx() ; i < playArea.get(card.getPlayIdx()).size() ; i++){
                                    card = playArea.get(card.getPlayIdx()).get(i);
                                    card.setPosition(card.getLastPosition());
                                }
                            }
                            else {
                                card.setPosition(card.getLastPosition());
                            }
                        }
                        else{
                            cardToPlay(playIdx , card);
                        }
                    }
                }
            }
            return true;
        }
    };

    private boolean checkIfInside(float touchX , float touchY , float startX , float endX , float startY , float endY) {
        return (touchX >= startX && touchX <= endX && touchY >= startY && touchY <= endY);
    }

    private void reFillDeck(){
        while(!hand.empty()){
            Card card = hand.peek();
            hand.pop();
            card.toDeck(deckPosition);
            deck.push(card);
        }
    }

    private void cardToHand(Card card){
        deck.pop();
        card.toHand(handPosition);
        hand.push(card);
    }

    private void cardToPlay(int targetIdx , Card card) {

        int currIdx = card.getPlayIdx();
        int cardInPlayIdx = card.getInPlayIdx();
        boolean isPlay = card.getPlay();
        boolean isFinish = card.getFinished();
        boolean isHand = card.getHand();

        for(int i = cardInPlayIdx ; i < playArea.get(currIdx).size() ; i++){
            card = playArea.get(currIdx).get(i);
            card.toPlayArea(targetIdx , playArea.get(targetIdx).size() , new Pair<Float, Float>(playAreaPosition.get(targetIdx).first ,
                    playAreaPosition.get(targetIdx).second + playArea.get(targetIdx).size() * cardParams.height / 4));
            playArea.get(targetIdx).add(card);
        }

        if(isPlay){
            for(int i = playArea.get(currIdx).size() - 1 ; i >= cardInPlayIdx ; i--){
                playArea.get(currIdx).remove(i);
            }
            if(!playArea.get(currIdx).isEmpty()){
                if(!playArea.get(currIdx).get(playArea.get(currIdx).size()-1).getFaceUp()){
                    playArea.get(currIdx).get(playArea.get(currIdx).size()-1).showCard();
                }
            }
        }
        else if(isHand){
            hand.pop();
        }
        else if(isFinish){
            suitsCard.get(suitIdx.get(card.getName())).pop();
        }
    }

   private void cardToSuits(int idx  , Card card){
        if(card.getPlay()){
            playArea.get(card.getPlayIdx()).remove(card.getInPlayIdx());
            if(!playArea.get(card.getPlayIdx()).isEmpty()){
                playArea.get(card.getPlayIdx()).get(playArea.get(card.getPlayIdx()).size() - 1).showCard();
            }
        }
        else if(card.getHand()){
            hand.pop();
        }
        card.toSuits(idx , suitsPosition.get(idx));
        suitsCard.get(idx).add(card);
    }


    private View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v , DragEvent event) {
            Log.i("PlayArea" , "hi");
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    Card draggedCard = (Card) event.getLocalState();
                    Card targetCard = ((Card) v);

                    if(targetCard.getPlay()){
                        if(targetCard.getNumber() == draggedCard.getNumber() && draggedCard.getRed() != targetCard.getRed()){
                            if(draggedCard.getPlay()){
                                for(int i = draggedCard.getInPlayIdx() ; i < playArea.get(draggedCard.getPlayIdx()).size() ; i++){
                                    draggedCard = playArea.get(draggedCard.getPlayIdx()).get(i);

                           //         cardToPlay(targetCard.getPlayIdx() , draggedCard);
                                }
                            }
                            else if(draggedCard.getFinished()){
                           //     cardToPlay(targetCard.getPlayIdx() , draggedCard);
                            }
                            else if(draggedCard.getHand()){
                             //   cardToPlay(targetCard.getPlayIdx() , draggedCard);
                            }
                        }
                    }

                    break;
            }
            return true;
        }
    };
}
