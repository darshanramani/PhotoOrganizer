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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
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
    val album: String,
    val image: Int,
    var isFavorite: Boolean = false
)

data class Album(
    val name: String,
    val coverImage: Int
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
    var isDarkMode by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
    ) {
        val photos = remember {
            mutableStateListOf(
                GalleryPhoto(1, "City", R.drawable.tb1),
                GalleryPhoto(2, "City", R.drawable.tb2),
                GalleryPhoto(3, "Lake", R.drawable.tb3),
                GalleryPhoto(4, "Lake", R.drawable.tb4),
                GalleryPhoto(5, "Nature", R.drawable.tb5),
                GalleryPhoto(6, "Nature", R.drawable.tb6),
                GalleryPhoto(7, "Campus", R.drawable.tb7),
                GalleryPhoto(8, "Campus", R.drawable.tb8),
                GalleryPhoto(9, "Food", R.drawable.tb9),
                GalleryPhoto(10, "Food", R.drawable.tb10),
                GalleryPhoto(11, "Nature", R.drawable.tb11)
            )
        }

        var selectedAlbum by remember { mutableStateOf<String?>(null) }
        var selectedPhoto by remember { mutableStateOf<GalleryPhoto?>(null) }

        when {
            selectedPhoto != null -> {
                PhotoDetailScreen(
                    photo = selectedPhoto!!,
                    isDarkMode = isDarkMode,
                    onThemeClick = { isDarkMode = !isDarkMode },
                    onBack = { selectedPhoto = null }
                )
            }

            selectedAlbum != null -> {
                AlbumPhotoScreen(
                    albumName = selectedAlbum!!,
                    photos = photos.filter { it.album == selectedAlbum },
                    isDarkMode = isDarkMode,
                    onThemeClick = { isDarkMode = !isDarkMode },
                    onBack = { selectedAlbum = null },
                    onPhotoClick = { selectedPhoto = it },
                    onFavoriteClick = { photo ->
                        val index = photos.indexOfFirst { it.id == photo.id }
                        if (index != -1) {
                            photos[index] = photos[index].copy(isFavorite = !photos[index].isFavorite)
                        }
                    }
                )
            }

            else -> {
                AlbumHomeScreen(
                    photos = photos,
                    isDarkMode = isDarkMode,
                    onThemeClick = { isDarkMode = !isDarkMode },
                    onAlbumClick = { selectedAlbum = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumHomeScreen(
    photos: List<GalleryPhoto>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onAlbumClick: (String) -> Unit
) {
    var isGridView by remember { mutableStateOf(true) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val allAlbums = listOf(
        Album("City", R.drawable.tb1),
        Album("Lake", R.drawable.tb3),
        Album("Nature", R.drawable.tb5),
        Album("Campus", R.drawable.tb7),
        Album("Food", R.drawable.tb9)
    )

    val albums = if (showFavoritesOnly) {
        allAlbums.filter { album ->
            photos.any { it.album == album.name && it.isFavorite }
        }
    } else {
        allAlbums
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thunder Bay Gallery", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onThemeClick) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp)
        ) {
            Text("Organize Thunder Bay memories by folders")

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    Text(if (showFavoritesOnly) "All" else "Favorites")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (albums.isEmpty()) {
                Text("No favorite folders yet. Open any folder and mark photos as favorite.")
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(albums) { album ->
                        AlbumGridCard(album, photos, onAlbumClick)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(albums) { album ->
                        AlbumListCard(album, photos, onAlbumClick)
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumGridCard(
    album: Album,
    photos: List<GalleryPhoto>,
    onAlbumClick: (String) -> Unit
) {
    val count = photos.count { it.album == album.name }
    val favCount = photos.count { it.album == album.name && it.isFavorite }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlbumClick(album.name) },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = album.coverImage),
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(album.name, fontWeight = FontWeight.Bold)
                Text("$count photos • $favCount favorites")
            }
        }
    }
}

@Composable
fun AlbumListCard(
    album: Album,
    photos: List<GalleryPhoto>,
    onAlbumClick: (String) -> Unit
) {
    val count = photos.count { it.album == album.name }
    val favCount = photos.count { it.album == album.name && it.isFavorite }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlbumClick(album.name) },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = album.coverImage),
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(album.name, fontWeight = FontWeight.Bold)
                Text("$count photos • $favCount favorites")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumPhotoScreen(
    albumName: String,
    photos: List<GalleryPhoto>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onBack: () -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit
) {
    var isGridView by remember { mutableStateOf(true) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val filteredPhotos = if (showFavoritesOnly) {
        photos.filter { it.isFavorite }
    } else {
        photos
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(albumName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onThemeClick) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme"
                        )
                    }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    Text(if (showFavoritesOnly) "All" else "Favorites")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredPhotos.isEmpty()) {
                Text("No favorite photos in this folder yet.")
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoGridCard(photo, onPhotoClick, onFavoriteClick)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoListCard(photo, onPhotoClick, onFavoriteClick)
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
        Box {
            Image(
                painter = painterResource(id = photo.image),
                contentDescription = "Thunder Bay photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(160.dp)
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
                contentDescription = "Thunder Bay photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = photo.album,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

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
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(photo.album) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onThemeClick) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme"
                        )
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
                contentDescription = "Thunder Bay photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Album: ${photo.album}", fontWeight = FontWeight.Bold)
            Text(
                if (photo.isFavorite)
                    "This photo is marked as favorite."
                else
                    "This photo is not favorite yet."
            )
        }
    }
}