package com.example.proyectomov.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.proyectomov.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_thin, FontWeight.Thin),
    Font(R.font.montserrat_extralight, FontWeight.ExtraLight),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_black, FontWeight.Black),
    Font(R.font.montserrat_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserrat_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserrat_blackitalic, FontWeight.Black, FontStyle.Italic),
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.7.sp
    ),
)