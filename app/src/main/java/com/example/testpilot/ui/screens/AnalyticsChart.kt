
package com.example.testpilot.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testpilot.ui.theme.NavySurface
import com.example.testpilot.ui.theme.TextSecondary

data class AnalyticsChartItem(
    val label: String,
    val value: Int,
    val color: Color
)


// =============================================
// HORIZONTAL DISTRIBUTION CHART
// =============================================

@Composable
fun AnalyticsDistributionCard(
    title: String,
    items: List<AnalyticsChartItem>
) {
    val total = items.sumOf { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            items.forEach { item ->

                val fraction = if (total > 0) {
                    (item.value.toFloat() / total)
                        .coerceIn(0f, 1f)
                } else {
                    0f
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.label,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Text(
                        text = item.value.toString(),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .background(
                            color = Color(0xFF303A52),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    if (fraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(9.dp)
                                .background(
                                    color = item.color,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
            }

            Text(
                text = "Total: $total",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}


// =============================================
// PIE CHART CARD
// =============================================

@Composable
fun AnalyticsPieChartCard(
    title: String,
    items: List<AnalyticsChartItem>
) {

    val total = items.sumOf { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                color = Color.White,
                fontSize = 17.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (total > 0) {

                Canvas(
                    modifier = Modifier.size(190.dp)
                ) {

                    var startAngle = -90f

                    items.forEach { item ->

                        if (item.value > 0) {

                            val sweepAngle =
                                (item.value.toFloat() / total) * 360f

                            drawArc(
                                color = item.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true
                            )

                            startAngle += sweepAngle
                        }
                    }
                }

            } else {

                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            items.forEach { item ->

                val percentage = if (total > 0) {
                    (item.value.toFloat() / total) * 100f
                } else {
                    0f
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .background(
                                    item.color,
                                    CircleShape
                                )
                        )

                        Text(
                            text = item.label,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 9.dp)
                        )
                    }

                    Text(
                        text = "${item.value} (${percentage.toInt()}%)",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: $total",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
