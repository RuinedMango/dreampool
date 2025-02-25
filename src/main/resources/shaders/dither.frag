#version 460 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D screenTexture;
uniform int levels; // e.g., 8 for 3-bit color

const int matrixSize = 4;
const int bayerMatrix[16] = int[](
     0,  8,  2, 10,
    12,  4, 14,  6,
     3, 11,  1,  9,
    15,  7, 13,  5
);

void main() {
    vec3 color = texture(screenTexture, TexCoords).rgb;
    
    // Calculate Bayer matrix index
    ivec2 coord = ivec2(gl_FragCoord.xy);
    int x = int(mod(float(coord.x), float(matrixSize)));
    int y = int(mod(float(coord.y), float(matrixSize)));
    int index = x + y * matrixSize;
    float threshold = float(bayerMatrix[index]) / float(matrixSize * matrixSize);
    
    // Apply dithering to each channel
    vec3 result;
    for (int i = 0; i < 3; i++) {
        float colorScaled = color[i] * (levels - 1);
        float fractional = fract(colorScaled);
        if (fractional > threshold) {
            result[i] = (floor(colorScaled) + 1.0) / (levels - 1);
        } else {
            result[i] = floor(colorScaled) / (levels - 1);
        }
    }
    
    FragColor = vec4(result, 1.0);
}