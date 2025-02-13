#version 460 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aTexCoord;
layout(location = 2) in vec3 aNormal;

out vec3 vsPos;          // Pass position to TCS
out vec2 vsTexCoord;     // Pass texture coordinate to TCS
out vec3 vsNormal;

void main() {
    vsPos = aPos;
    vsTexCoord = aTexCoord;
    vsNormal = aNormal;
}