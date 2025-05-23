#version 460 core
out vec4 FragColor;

noperspective in vec2 TexCoord;
in vec3 Normal; //Vertex normal
in float fogDensity;
in vec3 lighting; 

uniform sampler2D texture1;
uniform sampler2D texture2;
uniform vec4 fogColor;

void main() {
    // Mix textures and apply fog
    vec4 texColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
    texColor.rgb *= lighting;
    vec3 foggedColor = mix(texColor.rgb, fogColor.rgb, fogDensity);

    FragColor = vec4(foggedColor, texColor.a);
    
}