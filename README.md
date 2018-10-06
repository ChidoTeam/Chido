# Chido
for airtime transfer

[![](https://jitpack.io/v/ChidoTeam/chido.svg)](https://jitpack.io/#ChidoTeam/chido)

ChidoSDK wrapper for Chido services.

**Installation**

Add it in your root build.gradle at the end of repositories
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency 

```gradle
dependencies {
	        compile 'com.github.ChidoTeam:Chido:v1.3'
	}
```

**Services**
- Airtime transfer


**Usage**

Integrate **Send Airtime** feature in your application powered by the Chido  
```java
  public class MainActivity extends AppCompatActivity {

    Button apply_btn;
    ChidoSDK chidoSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apply_btn = (Button)findViewById(R.id.apply_btn);
        
        // Customer ID is the identifier on our CRB system. This can be a phone number or unique ID
        final String customerId = "333333";
        chidoSDK = new ChidoSDK(getString(R.string.userId), customerId, getString(R.string.auth_key), getString(R.string.auth_secret));

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chidoSDK.loanRequest(new ChidoSDK.LoanResultCallback(){
                    @Override
                    public void onSuccess() {
                      // Handle on success return from the Chido CRB
                    }

                    @Override
                    public void onFailed(String error_msg) {
                      // Handle on failed return from the Chido CRB
                    }

                }, MainActivity.this);
            }
        });
    }
    
    // Handles ChidoSDK onActivity result
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        chidoSDK.onResponse(arg0, arg1, arg2);
    }
}
```

