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
import androidx.compose.material.icons.filled.*
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

        val albumNames = remember {
            mutableStateListOf(
                "City",
                "Lake",
                "Nature",
                "Campus",
                "Food"
            )
        }

        val photos = remember {
            mutableStateListOf(


                GalleryPhoto(1, "Food", R.drawable.tb1),
                GalleryPhoto(2, "City", R.drawable.tb2),
                GalleryPhoto(3, "City", R.drawable.tb3),
                GalleryPhoto(4, "City", R.drawable.tb4),
                GalleryPhoto(5, "Campus", R.drawable.tb5),
                GalleryPhoto(6, "Food", R.drawable.tb6),
                GalleryPhoto(7, "Campus", R.drawable.tb7),
                GalleryPhoto(8, "Campus", R.drawable.tb8),
                GalleryPhoto(9, "Food", R.drawable.tb9),
                GalleryPhoto(10, "City", R.drawable.tb10),
                GalleryPhoto(11, "Campus", R.drawable.tb11),
                GalleryPhoto(12, "Nature", R.drawable.tb6),
                GalleryPhoto(13, "Lake", R.drawable.tb7),
                GalleryPhoto(14, "Food", R.drawable.tb8),
                GalleryPhoto(15, "Campus", R.drawable.tb9),
                GalleryPhoto(16, "Food", R.drawable.tb10),
                GalleryPhoto(17, "Campus", R.drawable.tb11)

            )
        }

        val availableImages = listOf(
            R.drawable.tb1,
            R.drawable.tb2,
            R.drawable.tb3,
            R.drawable.tb4,
            R.drawable.tb5,
            R.drawable.tb6,
            R.drawable.tb7,
            R.drawable.tb8,
            R.drawable.tb9,
            R.drawable.tb10,
            R.drawable.tb11
        )

        var showFolderView by remember { mutableStateOf(false) }

        var selectedAlbum by remember {
            mutableStateOf<String?>(null)
        }

        var selectedPhoto by remember {
            mutableStateOf<GalleryPhoto?>(null)
        }

        var slideshowPhotos by remember {
            mutableStateOf<List<GalleryPhoto>?>(null)
        }

        val toggleFavorite: (GalleryPhoto) -> Unit = { photo ->

            val index = photos.indexOfFirst {
                it.id == photo.id
            }

            if (index != -1) {

                photos[index] =
                    photos[index].copy(
                        isFavorite = !photos[index].isFavorite
                    )
            }
        }

        when {

            slideshowPhotos != null -> {

                SlideshowScreen(
                    photos = slideshowPhotos!!,
                    isDarkMode = isDarkMode,
                    onThemeClick = {
                        isDarkMode = !isDarkMode
                    },
                    onBack = {
                        slideshowPhotos = null
                    }
                )
            }

            selectedPhoto != null -> {

                PhotoDetailScreen(
                    photo = selectedPhoto!!,
                    isDarkMode = isDarkMode,
                    onThemeClick = {
                        isDarkMode = !isDarkMode
                    },
                    onBack = {
                        selectedPhoto = null
                    },
                    onDelete = {

                        photos.removeIf {
                            it.id == selectedPhoto!!.id
                        }

                        selectedPhoto = null
                    }
                )
            }

            selectedAlbum != null -> {

                AlbumPhotoScreen(
                    albumName = selectedAlbum!!,
                    photos = photos.filter {
                        it.album == selectedAlbum
                    },

                    availableImages = availableImages,

                    isDarkMode = isDarkMode,

                    onThemeClick = {
                        isDarkMode = !isDarkMode
                    },

                    onBack = {
                        selectedAlbum = null
                    },

                    onPhotoClick = {
                        selectedPhoto = it
                    },

                    onFavoriteClick = toggleFavorite,

                    onAddPhoto = { image ->

                        val newId =
                            (photos.maxOfOrNull { it.id } ?: 0) + 1

                        photos.add(
                            GalleryPhoto(
                                newId,
                                selectedAlbum!!,
                                image
                            )
                        )
                    },

                    onFolderSlideshow = {

                        val folderPhotos =
                            photos.filter {
                                it.album == selectedAlbum
                            }

                        if (folderPhotos.isNotEmpty()) {
                            slideshowPhotos = folderPhotos
                        }
                    }
                )
            }

            showFolderView -> {

                FolderHomeScreen(

                    albumNames = albumNames,

                    photos = photos,

                    isDarkMode = isDarkMode,

                    onThemeClick = {
                        isDarkMode = !isDarkMode
                    },

                    onBackToAllPhotos = {
                        showFolderView = false
                    },

                    onAlbumClick = {
                        selectedAlbum = it
                    },

                    onCreateFolder = { newName ->

                        if (
                            newName.isNotBlank()
                            &&
                            !albumNames.contains(newName)
                        ) {
                            albumNames.add(newName)
                        }
                    },

                    onDeleteFolder = { folderName ->

                        albumNames.remove(folderName)

                        val photosToRemove =
                            photos.filter {
                                it.album == folderName
                            }

                        photos.removeAll(photosToRemove)
                    }
                )
            }

            else -> {

                AllPhotosHomeScreen(

                    albumNames = albumNames,

                    photos = photos,

                    isDarkMode = isDarkMode,

                    onThemeClick = {
                        isDarkMode = !isDarkMode
                    },

                    onFolderViewClick = {
                        showFolderView = true
                    },

                    onPhotoClick = {
                        selectedPhoto = it
                    },

                    onFavoriteClick = toggleFavorite,

                    onAllSlideshow = {

                        if (photos.isNotEmpty()) {
                            slideshowPhotos = photos
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPhotosHomeScreen(
    albumNames: List<String>,
    photos: List<GalleryPhoto>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onFolderViewClick: () -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit,
    onAllSlideshow: () -> Unit
) {

    var isGridView by remember {
        mutableStateOf(true)
    }


    var columnCount by remember { mutableStateOf(2) }

    var showFavoritesOnly by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    val visiblePhotos = photos.filter {

        val searchMatch =
            it.album.contains(searchText, ignoreCase = true)

        val favMatch =
            !showFavoritesOnly || it.isFavorite

        searchMatch && favMatch
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Thunder Bay Gallery",
                        fontWeight = FontWeight.Bold
                    )
                },

                actions = {

                    IconButton(onClick = onThemeClick) {

                        Icon(
                            if (isDarkMode)
                                Icons.Default.LightMode
                            else
                                Icons.Default.DarkMode,

                            contentDescription = "Theme"
                        )
                    }
                }
            )
        },

        bottomBar = {

            Button(
                onClick = onAllSlideshow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Icon(
                    Icons.Default.Slideshow,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text("Slideshow All Photos")
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(14.dp)
        ) {

            StatsCard(
                albumsCount = albumNames.size,
                photosCount = photos.size,
                favoriteCount = photos.count {
                    it.isFavorite
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchText,

                onValueChange = {
                    searchText = it
                },

                label = {
                    Text("Search by folder name")
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                GridDropdownButton(
                    columnCount = columnCount,
                    onColumnChange = {
                        columnCount = it
                        isGridView = true
                    }
                )

                Button(
                    onClick = {
                        isGridView = false
                    }
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null
                    )

                    Text(" List")
                }

                Button(
                    onClick = {
                        showFavoritesOnly = !showFavoritesOnly
                    }
                ) {
                    Text(
                        if (showFavoritesOnly)
                            "All"
                        else
                            "Favorites"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onFolderViewClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Folder,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text("Open Folder View")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                if (showFavoritesOnly)
                    "Favorite Photos"
                else
                    "All Photos",

                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (visiblePhotos.isEmpty()) {

                Text("No photos found.")
            }

            else if (isGridView) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnCount),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(visiblePhotos) { photo ->

                        PhotoGridCard(
                            photo,
                            onPhotoClick,
                            onFavoriteClick
                        )
                    }
                }
            }

            else {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(visiblePhotos) { photo ->

                        PhotoListCard(
                            photo,
                            onPhotoClick,
                            onFavoriteClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderHomeScreen(
    albumNames: List<String>,
    photos: List<GalleryPhoto>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onBackToAllPhotos: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
    onDeleteFolder: (String) -> Unit
) {

    var isGridView by remember {
        mutableStateOf(true)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var newFolderName by remember {
        mutableStateOf("")
    }

    var folderToDelete by remember {
        mutableStateOf<String?>(null)
    }

    val filteredAlbums = albumNames.filter {
        it.contains(searchText, ignoreCase = true)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Folder View",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackToAllPhotos
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {

                    IconButton(onClick = onThemeClick) {

                        Icon(
                            if (isDarkMode)
                                Icons.Default.LightMode
                            else
                                Icons.Default.DarkMode,

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

            StatsCard(
                albumsCount = albumNames.size,
                photosCount = photos.size,
                favoriteCount = photos.count {
                    it.isFavorite
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchText,

                onValueChange = {
                    searchText = it
                },

                label = {
                    Text("Search folder")
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        isGridView = true
                    }
                ) {

                    Icon(
                        Icons.Default.GridView,
                        contentDescription = null
                    )

                    Text(" Grid")
                }

                Button(
                    onClick = {
                        isGridView = false
                    }
                ) {

                    Icon(
                        Icons.Default.List,
                        contentDescription = null
                    )

                    Text(" List")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    showCreateDialog = true
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.CreateNewFolder,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text("Create Folder")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredAlbums.isEmpty()) {

                Text("No folders found.")
            }

            else if (isGridView) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp),

                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {

                    items(filteredAlbums) { album ->

                        AlbumGridCard(
                            album = album,
                            photos = photos,
                            onAlbumClick = onAlbumClick,
                            onDeleteClick = {
                                folderToDelete = album
                            }
                        )
                    }
                }
            }

            else {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(filteredAlbums) { album ->

                        AlbumListCard(
                            album = album,
                            photos = photos,
                            onAlbumClick = onAlbumClick,
                            onDeleteClick = {
                                folderToDelete = album
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(albumsCount: Int, photosCount: Int, favoriteCount: Int) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Folders", albumsCount.toString())
            StatItem("Photos", photosCount.toString())
            StatItem("Favorites", favoriteCount.toString())
        }
    }
}

@Composable
fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun AlbumGridCard(
    album: String,
    photos: List<GalleryPhoto>,
    onAlbumClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val albumPhotos = photos.filter { it.album == album }
    val coverImage = albumPhotos.lastOrNull()?.image

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box(modifier = Modifier.clickable { onAlbumClick(album) }) {
                if (coverImage != null) {
                    Image(
                        painter = painterResource(id = coverImage),
                        contentDescription = album,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth()
                    )
                } else {
                    EmptyFolderBox()
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete folder")
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(album, fontWeight = FontWeight.Bold)
                Text("${albumPhotos.size} photos")
            }
        }
    }
}

@Composable
fun AlbumListCard(
    album: String,
    photos: List<GalleryPhoto>,
    onAlbumClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val albumPhotos = photos.filter { it.album == album }
    val coverImage = albumPhotos.lastOrNull()?.image

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .clickable { onAlbumClick(album) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (coverImage != null) {
                Image(
                    painter = painterResource(id = coverImage),
                    contentDescription = album,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(45.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(album, fontWeight = FontWeight.Bold)
                Text("${albumPhotos.size} photos")
            }

            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete folder")
            }
        }
    }
}

@Composable
fun EmptyFolderBox() {
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(50.dp))
            Text("Empty Folder")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumPhotoScreen(
    albumName: String,
    photos: List<GalleryPhoto>,
    availableImages: List<Int>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onBack: () -> Unit,
    onPhotoClick: (GalleryPhoto) -> Unit,
    onFavoriteClick: (GalleryPhoto) -> Unit,
    onAddPhoto: (Int) -> Unit,
    onFolderSlideshow: () -> Unit
) {
    var isGridView by remember { mutableStateOf(true) }
    var columnCount by remember { mutableStateOf(2) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredPhotos = if (showFavoritesOnly) photos.filter { it.isFavorite } else photos

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
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(12.dp)) {
                Button(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onFolderSlideshow, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Slideshow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Slideshow This Folder")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GridDropdownButton(
                    columnCount = columnCount,
                    onColumnChange = {
                        columnCount = it
                        isGridView = true
                    }
                )

                ColumnDropdown(
                    columnCount = columnCount,
                    onColumnChange = { columnCount = it }
                )

                Button(onClick = { isGridView = false }) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Text(" List")
                }

                Button(onClick = { showFavoritesOnly = !showFavoritesOnly }) {
                    Text(if (showFavoritesOnly) "All" else "Favorites")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredPhotos.isEmpty()) {
                Text("No photos in this folder yet.")
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnCount),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoGridCard(photo, onPhotoClick, onFavoriteClick)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredPhotos) { photo ->
                        PhotoListCard(photo, onPhotoClick, onFavoriteClick)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Choose Photo") },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(availableImages) { image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onAddPhoto(image)
                                    showAddDialog = false
                                }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                contentDescription = null,
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
                    if (photo.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
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
                contentDescription = null,
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
                    if (photo.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
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
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
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
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Folder: ${photo.album}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Remove Photo")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Photo") },
            text = { Text("Do you want to remove this photo from the folder?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideshowScreen(
    photos: List<GalleryPhoto>,
    isDarkMode: Boolean,
    onThemeClick: () -> Unit,
    onBack: () -> Unit
) {
    var index by remember { mutableStateOf(0) }
    val photo = photos[index]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Slideshow") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onThemeClick) {
                        Icon(
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = photo.image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .clip(RoundedCornerShape(22.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Folder: ${photo.album}", fontWeight = FontWeight.Bold)
            Text("${index + 1} / ${photos.size}")

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        index = if (index == 0) photos.size - 1 else index - 1
                    }
                ) {
                    Icon(Icons.Default.NavigateBefore, contentDescription = null)
                    Text("Previous")
                }

                Button(
                    onClick = {
                        index = if (index == photos.size - 1) 0 else index + 1
                    }
                ) {
                    Text("Next")
                    Icon(Icons.Default.NavigateNext, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun ColumnDropdown(
    columnCount: Int,
    onColumnChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text("$columnCount Col")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf(2, 3, 4).forEach { count ->
                DropdownMenuItem(
                    text = { Text("$count Columns") },
                    onClick = {
                        onColumnChange(count)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun GridDropdownButton(
    columnCount: Int,
    onColumnChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Icon(Icons.Default.GridView, contentDescription = null)
            Text(" $columnCount")
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf(2, 3, 4).forEach { count ->
                DropdownMenuItem(
                    text = { Text("Grid $count Columns") },
                    onClick = {
                        onColumnChange(count)
                        expanded = false
                    }
                )
            }
        }
    }
}