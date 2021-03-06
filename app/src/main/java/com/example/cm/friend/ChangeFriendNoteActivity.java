package com.example.cm.friend;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cm.R;
import com.example.cm.myInfo.FriendInfo;
import com.example.cm.theme.ThemeColor;
import com.example.cm.util.Connect;
import com.example.cm.util.MessageManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.RosterEntry;

public class ChangeFriendNoteActivity extends AppCompatActivity {
private TextView saveChangeNoteTV;
private EditText changeNoteET;
private String userName;
private FriendInfo curFriendInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_friend_note);
        Toolbar toolbar=(Toolbar)findViewById(R.id.changeFriendNote_toolbar);
        ThemeColor.setTheme(ChangeFriendNoteActivity.this,toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        }
        init();
    }
    public void init(){
        saveChangeNoteTV=findViewById(R.id.save_changeFriendNote_tv);
        saveChangeNoteTV.setBackgroundColor(Color.parseColor(ThemeColor.backColorStr));
        changeNoteET=findViewById(R.id.changeFriendNote_et);
        userName=getIntent().getStringExtra("userName");
        curFriendInfo=MessageManager.getFriendInfoFromContantList(userName);
        saveChangeNoteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note=changeNoteET.getText().toString().trim();
                if(note!=null){
                    if(curFriendInfo!=null) {
                        RosterEntry entry = Connect.getRoster().getEntry(userName + "@" + Connect.SERVERNAME);
                        try {
                            if (entry == null) {
                                Toast.makeText(ChangeFriendNoteActivity.this, "修改备注失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            entry.setName(note);
                            MessageManager.getDataBaseHelp().changeFriendNote(userName, note);
                            curFriendInfo.setNoteName(note);
                            String pinYin = Cn2Spell.getPinYin(note);
                            String firstLetter = Cn2Spell.getPinYinFirstLetter(pinYin);
                            curFriendInfo.setPinyin(pinYin);
                            curFriendInfo.setFirstLetter(firstLetter);
                            MessageManager.setContantListChanged(true);
                            MessageManager.setHaveNewMessage(true);
                            Toast.makeText(ChangeFriendNoteActivity.this, "修改备注成功", Toast.LENGTH_SHORT).show();
                        } catch (SmackException.NotConnectedException e) {
                            Toast.makeText(ChangeFriendNoteActivity.this, "修改备注失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (SmackException.NoResponseException e) {
                            Toast.makeText(ChangeFriendNoteActivity.this, "修改备注失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            Toast.makeText(ChangeFriendNoteActivity.this, "修改备注失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(ChangeFriendNoteActivity.this,"您还没有此好友",Toast.LENGTH_SHORT).show();
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
