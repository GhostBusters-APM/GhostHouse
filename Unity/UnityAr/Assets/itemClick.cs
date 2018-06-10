using UnityEngine;

public class itemClick : MonoBehaviour {

	
    private bool change = false;

    void Create() {

    }

    void Update() {

        if ((Input.touchCount > 0) && (Input.GetTouch(0).phase == TouchPhase.Began))
        {
            Ray raycast = Camera.main.ScreenPointToRay(Input.GetTouch(0).position);
            RaycastHit raycastHit;
            if (Physics.Raycast(raycast, out raycastHit))
            {
                Debug.Log("Something Hit");
                if (raycastHit.collider.name == "Switch")
                {
                    Debug.Log("Clicked changing color");
                    change = !change;
                    AndroidJavaObject arPlugin = new AndroidJavaObject("com.github.ghostbusters.ghosthouse.helper.unity.ArPlugin");
                    arPlugin.Call("call", new AndroidPluginCallback());

                }
                               
            }
        }
    }


    class AndroidPluginCallback : AndroidJavaProxy
    {
        public AndroidPluginCallback() : base("com.github.ghostbusters.ghosthouse.helper.unity.ArPluginCallback") { }

        public void onSuccess(string message)
        {
            Debug.Log("ENTER callback onSuccess: " + message);
        }
        public void onError(string errorMessage)
        {
            Debug.Log("ENTER callback onError: " + errorMessage);
        }
    }


}
