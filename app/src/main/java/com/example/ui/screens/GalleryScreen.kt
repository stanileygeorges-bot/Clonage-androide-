package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.GalleryPhotoEntity
import com.example.ui.theme.*

@Composable
fun GalleryScreen(
    phoneId: Int,
    photos: List<GalleryPhotoEntity>,
    onAddPhoto: (title: String, uri: String, category: String) -> Unit,
    onDeletePhoto: (GalleryPhotoEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = if (phoneId == 1) CyberCyan else NeonViolet
    var selectedPhotoForPreview by remember { mutableStateOf<GalleryPhotoEntity?>(null) }

    // Android zero-permission Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onAddPhoto("Sary ${photos.size + 1}", uri.toString(), "Vault")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                containerColor = accentColor,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Hampiditra sary"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Miverina",
                        tint = Color.White
                    )
                }
                Text(
                    text = if (phoneId == 1) "Sary (Finday 1)" else "Vata Miafina (Clonage 2)",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Add demo sample photo quick button
                TextButton(
                    onClick = {
                        onAddPhoto("Demo Shot ${photos.size + 1}", "drawable/wp_phone1", "Screenshot")
                    }
                ) {
                    Text("+ Demo", color = accentColor, fontSize = 12.sp)
                }
            }

            Divider(color = Color.White.copy(alpha = 0.08f), thickness = 1.dp)

            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (phoneId == 1) "Tsy misy sary ato" else "Vata miafina madio (Tsy misy sary)",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tsindrio ny bokotra '+' raha hampiditra sary manokana",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(photos) { photo ->
                        GalleryGridItem(
                            photo = photo,
                            onClick = { selectedPhotoForPreview = photo }
                        )
                    }
                }
            }
        }
    }

    // Photo Preview Dialog
    selectedPhotoForPreview?.let { photo ->
        AlertDialog(
            onDismissRequest = { selectedPhotoForPreview = null },
            title = {
                Text(text = photo.title, color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSlate),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photo.imagePathOrUri.startsWith("drawable/")) {
                            Image(
                                painter = painterResource(id = R.drawable.wp_phone1),
                                contentDescription = photo.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = photo.imagePathOrUri,
                                contentDescription = photo.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Text(
                        text = "Sokajy: ${photo.category}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePhoto(photo)
                        selectedPhotoForPreview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Fafao")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPhotoForPreview = null }) {
                    Text("Hidio", color = Color.White)
                }
            },
            containerColor = CardSlate
        )
    }
}

@Composable
private fun GalleryGridItem(
    photo: GalleryPhotoEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(CardSlate)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (photo.imagePathOrUri.startsWith("drawable/")) {
            Image(
                painter = painterResource(id = R.drawable.wp_phone1),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = photo.imagePathOrUri,
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
