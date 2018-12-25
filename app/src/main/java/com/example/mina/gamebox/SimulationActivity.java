package com.example.mina.gamebox;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

public class SimulationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SimulationView simulationView;
    Button addDel , balance, delete;
    BST bst;
    AVLTree avl;
    EditText nodeValue;
    boolean BST , AVL , STACK , QUEUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        STACK = QUEUE = AVL = BST = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nodeValue = (EditText) findViewById(R.id.nodeValue);

        addDel = (Button) findViewById(R.id.addDelButton);
        addDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AVL){
                    try{
                        avl.insert(Integer.parseInt(nodeValue.getText().toString()));
                        avl.setTree();
                        simulationView.simulateAVL(avl.root);
                    }
                    catch(NumberFormatException e) {
                        Toast.makeText(getBaseContext() , "Wrong Input" , Toast.LENGTH_SHORT).show();
                    }
                }
                else if(BST){
                    try{
                        bst.insert(Integer.parseInt(nodeValue.getText().toString()));
                        bst.setTree();
                        simulationView.simulateBST(bst.root);
                    }
                    catch(NumberFormatException e) {
                        Toast.makeText(getBaseContext() , "Wrong Input" , Toast.LENGTH_SHORT).show();
                    }
                }
                else if(QUEUE){

                }
                else if(STACK){

                }
                else {
                    Toast.makeText(getBaseContext() , "Please Choose A Data Structure to simulate" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete = (Button) findViewById(R.id.deleteNodeButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BST){
                    try {
                        bst.delete(Integer.parseInt(nodeValue.getText().toString()));
                        bst.setTree();
                        simulationView.simulateBST(bst.root);
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(getBaseContext() , "Wrong Input" , Toast.LENGTH_SHORT).show();
                    }
                }
                else if(AVL){
                    try {
                        avl.delete(Integer.parseInt(nodeValue.getText().toString()));
                        avl.setTree();
                        simulationView.simulateAVL(avl.root);
                    }
                    catch (NumberFormatException e){
                        Toast.makeText(getBaseContext() , "Wrong Input" , Toast.LENGTH_SHORT).show();
                    }
                }
                else if(STACK){

                }
                else if(QUEUE){

                }
                else {
                    Toast.makeText(getBaseContext() , "Please Choose A Data Structure to simulate" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        balance = (Button) findViewById(R.id.balance);
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bst.balance();
                bst.setTree();
                simulationView.simulateBST(bst.root);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus && simulationView == null) {
            simulationView = (SimulationView) findViewById(R.id.simulateView);
            Display display = getWindowManager().getDefaultDisplay();
            simulationView.initialize(display);

            bst = new BST(getBaseContext());
            avl = new AVLTree(getBaseContext());
            //new stack
            //new queue
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.BST) {
            balance.setVisibility(View.VISIBLE);
            simulationView.clear();
            BST = true;
            STACK = QUEUE = AVL = false;
        } else if (id == R.id.AVL) {
            balance.setVisibility(View.INVISIBLE);
            simulationView.clear();
            AVL = true;
            STACK = QUEUE = BST = false;
        } else if (id == R.id.stack) {
            balance.setVisibility(View.INVISIBLE);
            simulationView.clear();
            STACK = true;
            BST = QUEUE = AVL = false;
        } else if (id == R.id.queue) {
            balance.setVisibility(View.INVISIBLE);
            simulationView.clear();
            QUEUE = true;
            STACK = BST = AVL = false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
