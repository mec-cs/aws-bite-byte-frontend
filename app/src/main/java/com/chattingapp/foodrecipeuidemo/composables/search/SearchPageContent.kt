
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chattingapp.foodrecipeuidemo.R
import com.chattingapp.foodrecipeuidemo.entity.SearchCriteria
import com.chattingapp.foodrecipeuidemo.entity.UserProfile
import com.chattingapp.foodrecipeuidemo.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchPageCall()
        }
    }
}


@Composable
fun SearchPageCall() {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var isRecipeSelected by remember { mutableStateOf(true) }
    var isImageRendered by remember { mutableStateOf(false) }
    var userProfiles by remember { mutableStateOf(listOf<UserProfile>()) }
    val recipes = listOf(
        Recipe(1, "Spaghetti Carbonara", R.drawable.spaghetti_carbonara),
        Recipe(2, "Spaghetti Napolitana", R.drawable.spaghetti_napolitana),
        Recipe(3, "Spaghetti Bolognese", R.drawable.spaghetti_bolognese),
        Recipe(4, "Spaghetti with Sardines", R.drawable.spaghetti_sardines),
        Recipe(5, "Spaghetti Puttanesca", R.drawable.spaghetti_puttanesca)
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBar(searchText, isRecipeSelected) { newText ->
            searchText = newText
            val trimmedText = newText.text.trim()
            if (trimmedText.isNotEmpty()) {
                Log.d("SearchPageCall", "Search box value: $trimmedText")

                var searchCriteria = SearchCriteria(trimmedText, 0)
                isImageRendered = false
                RetrofitHelper.apiService.getUsersByUsername(searchCriteria) // Pass username and page as query parameters
                    .enqueue(object : Callback<List<UserProfile>> {
                        override fun onResponse(call: Call<List<UserProfile>>, response: Response<List<UserProfile>>) {
                            if (response.isSuccessful) {
                                //isImageRendered = false
                                userProfiles = response.body() ?: listOf()
                                val profilePictures: List<String> = userProfiles.mapNotNull { it.profilePicture }

                                RetrofitHelper.apiService.getProfilePicturesList(profilePictures) // Pass the list of profile picture URLs
                                    .enqueue(object : Callback<List<String>> {

                                        override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                            if (response.isSuccessful) {
                                                val encodedStrings: List<String> = response.body() ?: listOf()

                                                // Replace the profile picture URLs with the decoded strings
                                                userProfiles.forEachIndexed { index, userProfile ->
                                                    val encodedString = encodedStrings.getOrNull(index)
                                                    if (encodedString != null) {
                                                        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
                                                        val bitm = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                                        userProfile.bm = bitm
                                                        //isImageRendered = true
                                                    }
                                                }
                                                isImageRendered = true

                                            } else {
                                                Log.e("ProfilePictureFetch", "Error: ${response.errorBody()?.string()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                            Log.e("ProfilePictureFetch", "Cannot fetch profile pictures", t)
                                        }
                                    })
                            } else {
                                Log.e("SearchPageCall", "Error: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<List<UserProfile>>, t: Throwable) {
                            Log.e("SearchPageCall", "Network request failed", t)
                        }
                    })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ToggleView(isRecipeSelected) { isSelected ->
            isRecipeSelected = isSelected
            Log.d("SearchPageCall", if (isSelected) "Recipes selected" else "Users selected")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isRecipeSelected) {
            RecipeList(recipes.filter { it.name.contains(searchText.text.trim(), ignoreCase = true) })
        } else {
            UserProfileList(userProfiles, isImageRendered)
        }
    }
}

@Composable
fun SearchBar(searchText: TextFieldValue, isRecipeSelected: Boolean, onTextChange: (TextFieldValue) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .padding(8.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        )
        if (isRecipeSelected) {
            IconButton(onClick = { /* Handle filter click */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_filter), contentDescription = "Filter")
            }
        }
    }
}

@Composable
fun ToggleView(isRecipeSelected: Boolean, onSelectionChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { onSelectionChange(true) }, colors = ButtonDefaults.buttonColors(backgroundColor = if (isRecipeSelected) Color.Gray else Color.LightGray)) {
            Text("Recipes")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSelectionChange(false) }, colors = ButtonDefaults.buttonColors(backgroundColor = if (isRecipeSelected) Color.LightGray else Color.Gray)) {
            Text("Users")
        }
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>) {
    Column {
        recipes.forEach { recipe ->
            RecipeItem(recipe)
            Divider()
        }
    }
}

@Composable
fun UserProfileList(userProfiles: List<UserProfile>, isImgRendered: Boolean) {
    Column {
        userProfiles.forEach { userProfile ->
            UserProfileItem(userProfile, isImgRendered)
            Divider()
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(recipe.number.toString(), fontSize = 20.sp, modifier = Modifier.width(32.dp))
        Text(recipe.name, fontSize = 20.sp, modifier = Modifier.weight(1f))
        Image(painter = painterResource(id = recipe.image), contentDescription = recipe.name, modifier = Modifier.size(40.dp), contentScale = ContentScale.Crop)
    }
}

@Composable
fun UserProfileItem(userProfile: UserProfile, isImgRendered: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(userProfile.id.toString(), fontSize = 20.sp, modifier = Modifier.width(32.dp))
        Text(userProfile.username ?: "", fontSize = 20.sp, modifier = Modifier.weight(1f))
        if(isImgRendered) {
            userProfile.bm?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null, // Provide a content description if needed
                    modifier = Modifier
                        .size(50.dp)
                )
            }
        }

    }
}

data class Recipe(val number: Int, val name: String, val image: Int)
