package controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.OnFailureListener;
import com.google.firebase.tasks.OnSuccessListener;
import firebaseFiles.FireBaseDatabase;
import firebaseFiles.User;
import models.Allergy;
import models.wrapper_models.Allergies;
import org.sql2o.Sql2o;
import repositories.UserAllergyRepository;
import repositories.repositoryInterfaces.IUserAllergyRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static JsonUtil.JsonUtil.fromJson;
import static JsonUtil.JsonUtil.json;
import static JsonUtil.JsonUtil.toJson;
import static spark.Spark.*;

/**
 * Created by Kaempe on 03-04-2017.
 */
public class AllergyController
{
    private IUserAllergyRepository allergyRepository;

    public AllergyController(Sql2o sql2o)
    {
        allergyRepository = new UserAllergyRepository(sql2o);

        get("/allergies", (req, res) ->
        {
            Collection<Allergy> allergies = allergyRepository.getAll();
            if (allergies.size() != 0){
                res.status(200);
                return new Allergies(allergies);
            }
            res.status(200);
            return new String("No allergies found in the database");
        }, json());

        get("/allergies/user", (req, res) ->
        {
            String userId = req.attribute("userId");
            Collection<Allergy> allergies = allergyRepository.getAll(userId);
            if (allergies.size() != 0){
                res.status(200);
                return new Allergies(allergies);
            }
            res.status(200);
            return new String("No allergies found for the user " + userId);
        }, json());

        get("/allergies/search/:name", (req, res) ->
        {
            Map<String, String> search = new HashMap<>();
            String nameSearch = null;
            try{
                nameSearch = req.params(":name");

            }catch (Exception e){
                return new String("Invalid search parameter");
            }
            if (nameSearch == null) return new String("Invalid parameter name found in the request");
            search.put("name", nameSearch);
            Collection<Allergy> allergies = allergyRepository.getAll(search);
            if (allergies.size() != 0){
                res.status(200);
                return new Allergies(allergies);
            }
            res.status(200);
            return new String("No allergies found with the name:  " + nameSearch);
        }, json());

        get("/allergies/:id", (req, res) -> {
            int id ;
            try{
                id = Integer.parseInt(req.params(":id"));
            }catch (Exception e)
            {
                res.status(400);
                return new String("The id must be an integer");
            }
            Allergy allergy = allergyRepository.get(id);

            if (allergy != null) {
                res.status(200);
                return allergy;
            }
            res.status(200);
            return new String("No allergy with id "+ id +" found");
        }, json());

        put("/allergies",(req,res) -> {

            String userId = req.attribute("userId");
            Allergies allergies = null;

            try{
                allergies = fromJson(req.body(),Allergies.class);
            }catch (Exception e){
                return new String("Invalid request body");
            }

            boolean result = allergyRepository.updateRelations(userId,allergies.getAllergies());
            if (result)
            {
                res.status(200);
                return new String("Allergies for the user with id " + userId + " have been updated");
            }
            res.status(400);
            return new String("Could'nt update the allergies for the user with id " + userId);
        },json());

        before((req,res) -> {
            String authToken = null;
            try{
                authToken = req.headers("Authorization");
                if (authToken == null || authToken == "")
                {
                    throw new IllegalArgumentException("Wrong authentication");
                }
            }catch (Exception e){
                throw new IllegalArgumentException("Wrong authentication");
            }

            try
            {
                HashMap<String,Boolean> response = new HashMap<String, Boolean>();
                response.put("validResponse",false);
                response.put("validToken",false);
                FirebaseAuth.getInstance().verifyIdToken(authToken)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseToken>() {
                            @Override
                            public void onSuccess(FirebaseToken decodedToken) {
                                String uid = decodedToken.getUid();
                                req.attribute("userId",uid);

                                User user = FireBaseDatabase.getUserInfo(uid);
                                req.attribute("role",user.getRole());

                                response.put("validToken",true);
                                response.put("validResponse",true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e)  {
                                req.attribute("userId","Markus");
                                response.put("validToken",true );
                                response.put("validResponse",true);
                                req.attribute("role","FUCK OF!!!");
                            }
                        });
                long timer = System.currentTimeMillis();
                while (!response.get("validResponse")){
                    if (System.currentTimeMillis() - timer >= 3000){
                        throw new TimeoutException("Login service timed out");
                    }
                }
                if (!response.get("validToken")){
                    throw new IllegalArgumentException("Invalid Token");
                }
                if (req.attribute("userId") == null){
                    throw new IllegalArgumentException("Could'nt retrieve userId");
                }
                if (!(req.attribute("role").equals("client"))){
                    throw new IllegalArgumentException("user access denied");
                }
            }
            catch (IllegalArgumentException e)
            {
                throw new IllegalArgumentException(e.getMessage());
            }
        });

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(e.getMessage()));
            res.type("application/json");
        });
    }
}
