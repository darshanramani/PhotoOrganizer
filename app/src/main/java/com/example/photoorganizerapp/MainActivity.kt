package com.example.photoorganizerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class GalleryPhoto(
    val id: Int,
    val title: String,
    val tag: String,
    val image: Int,
    var isFavorite: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThunderBayGalleryApp()
        }
    }
}

@Composable
fun ThunderBayGalleryApp() {
    MaterialTheme {
        var selectedPhoto by remember { mutableStateOf<GalleryPhoto?>(null) }

        val photos = remember {
            mutableStateListOf(
                GalleryPhoto(1, "Thunder Bay View", "Nature", R.drawable.tb1),
                GalleryPhoto(2, "Lake Superior", "Lake", R.drawable.tb2),
                GalleryPhoto(3, "City Walk", "City", R.drawable.tb3),
                GalleryPhoto(4, "Campus Moment", "Campus", R.drawable.tb4),
                GalleryPhoto(5, "Food Spot", "Food", R.drawable.tb5),
                GalleryPhoto(6, "Nature Trail", "Nature", R.drawable.tb6),
                GalleryPhoto(7, "Waterfront Area", "Lake", R.drawable.tb7),
                GalleryPhoto(8, "Downtown Thunder Bay", "City", R.drawable.tb8),
                GalleryPhoto(9, "Student Life", "Campus", R.drawable.tb9),
                GalleryPhoto(10, "Local Food", "Food", R.drawable.tb10),
                GalleryPhoto(11, "Evening View", "Nature", R.drawable.tb11)
            )
        }

        if (selectedPhoto == null) {
            GalleryHomeScreen(
                photos = photos,
                onPhotoClick = { selectedPhoto = it },
                onFavoriteClick = { photo ->
                    val index = photos.indexOfFirst { it.id == photo.id }
                    if (index != -1) {
                        photos[index] = photos[index].copy(isFavorite = !photos[index].isFavorite)
                    }
                }
            )
        } else {
            PhotoDetailScreen(
                photo = selectedPhoto!!,
                onBack = { selectedPhoto = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryHomeScreen(
    photos: List<GalleryPhoto>,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit
) {
    var selectedTag by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(true) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val tags = listOf("All", "Nature", "Lake", "City", "Food", "Campus")

    val filteredPhotos = photos.filter { photo ->
        val tagMatch = selectedTag == "All" || photo.tag == selectedTag
        val favMatch = !showFavoritesOnly || photo.isFavorite
        tagMatch && favMatch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thunder Bay Gallery",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Text(
                text = "Organize your Thunder Bay memories",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { isGridView = true }) {
                    Icon(Icons.Default.GridView, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grid")
                }

                Button(onClick = { isGridView = false }) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("List")
                }

                Button(onClick = { showFavoritesOnly = !showFavoritesOnly }) {
                    Text(if (showFavoritesOnly) "All Photos" else "Favorites")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tags.forEach { tag ->
                    AssistChip(
                        onClick = { selectedTag = tag },
                        label = { Text(tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoGridCard(
                            photo = photo,
                            onPhotoClick = onPhotoClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoListCard(
                            photo = photo,
                            onPhotoClick = onPhotoClick,
                            onFavoriteClick = onFavoriteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoGridCard(
    photo: GalleryPhoto,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPhotoClick(photo) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = photo.image),
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                )

                IconButton(
                    onClick = { onFavoriteClick(photo) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (photo.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite"
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(photo.title, fontWeight = FontWeight.Bold)
                Text(photo.tag, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PhotoListCard(
    photo: GalleryPhoto,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPhotoClick(photo) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = photo.image),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(photo.title, fontWeight = FontWeight.Bold)
                Text("Tag: ${photo.tag}")
            }

            IconButton(onClick = { onFavoriteClick(photo) }) {
                Icon(
                    imageVector = if (photo.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    photo: GalleryPhoto,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photo.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = photo.image),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(photo.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Category: ${photo.tag}")
            Text(if (photo.isFavorite) "Marked as favorite photo" else "Not added to favorites yet")
        }
    }
}