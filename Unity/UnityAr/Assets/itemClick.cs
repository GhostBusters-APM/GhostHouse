using UnityEngine;

public class itemClick : MonoBehaviour {

	private AndroidJavaObject jo;
    public  Color color1;
    public Color color2;
    private bool change = false;

    void Create() {
         //jo = new AndroidJavaObject("android.content.res.Configuration");
        
    }
    // Update is called once per frame
    void Update() {

        if ((Input.touchCount > 0) && (Input.GetTouch(0).phase == TouchPhase.Began))
        {
            Ray raycast = Camera.main.ScreenPointToRay(Input.GetTouch(0).position);
            RaycastHit raycastHit;
            if (Physics.Raycast(raycast, out raycastHit))
            {
                Debug.Log("Something Hit");
                if (raycastHit.collider.name == "Cube")
                {
                    Debug.Log("Clicked changing color");
                    GetComponent<Renderer>().material.color=change ? color1 : color2;
                    change = !change;

                }
                               
            }
        }

                
            //clicked = false;
            //jo.Call("action",id,true);
            
        
    }

    
}
