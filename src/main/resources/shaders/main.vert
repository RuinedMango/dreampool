#version 460 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aTexCoord;

out vec3 vsPos;          // Pass position to TCS
out vec2 vsTexCoord;     // Pass texture coordinate to TCS

void main() {
    vsPos = aPos;
    vsTexCoord = aTexCoord;
}