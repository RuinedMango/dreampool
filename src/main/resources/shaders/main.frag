#version 460 core
out vec4 FragColor;

noperspective in vec2 TexCoord;
in vec3 Normal;
in float fogDensity;
in vec3 eyePos;

uniform sampler2D texture1;
uniform sampler2D texture2;
uniform vec4 fogColor;
uniform bool flatlight;
uniform vec3 lightDir;
uniform vec3 ambientColor;
uniform vec3 diffuseColor;

void main() {
    // Mix textures and apply fog
    vec4 texColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
    vec3 foggedColor = mix(texColor.rgb, fogColor.rgb, fogDensity);
    vec3 finalColor = foggedColor * lighting;

    FragColor = vec4(finalColor, texColor.a);
}