package com.example.cm.friend;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;

public class ChangeFriendNoteActivity extends AppCompatActivity {
private Button saveChangeNoteBtn;
private EditText changeNoteET;
private String userName;
private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_friend_note);
        Toolbar toolbar=(Toolbar)findViewById(R.id.changeFriendNote_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        }
        init();
    }
    public void init(){
        saveChangeNoteBtn=findViewById(R.id.save_changeFriendNote_btn);
        changeNoteET=findViewById(R.id.changeFriendNote_et);
        userName=getIntent().getStringExtra("userName");
        position=getIntent().getIntExtra("position",0);
        saveChangeNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note=changeNoteET.getText().toString().trim();
                if(note!=null){
                    RosterEntry entry= Connect.getRoster().getEntry(userName+"@"+Connect.SERVERNAME);
                    try {
                        if(entry==null){
                            Toast.makeText(ChangeFriendNoteActivity.this,"修改备注失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        entry.setName(note);
                        MessageManager.getDataBaseHelp().changeFriendNote(userName,note);
                        MessageManager.getContantFriendInfoList().get(position).setNoteName(note);
                        String pinYin=Cn2Spell.getPinYin(note);
                        String firstLetter=Cn2Spell.getPinYinFirstLetter(pinYin);
                        MessageManager.getContantFriendInfoList().get(position).setPinyin(pinYin);
                        MessageManager.getContantFriendInfoList().get(position).setFirstLetter(firstLetter);
                        MessageManager.setContantListChanged(true);
                        MessageManager.setHaveNewMessage(true);
                        Toast.makeText(ChangeFriendNoteActivity.this,"修改备注成功",Toast.LENGTH_SHORT).show();
                    } catch (SmackException.NotConnectedException e) {
                        Toast.makeText(ChangeFriendNoteActivity.this,"修改备注失败",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        Toast.makeText(ChangeFriendNoteActivity.this,"修改备注失败",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        Toast.makeText(ChangeFriendNoteActivity.this,"修改备注失败",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ChangeFriendNoteActivity.this,"备注不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{this.finish();}break;
            default:break;
        }
        return true;
    }
}
