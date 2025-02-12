#version 460 core
out vec4 FragColor;

noperspective in vec2 TexCoord;
in float fogDensity;

// texture samplers
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform vec4 fogColor;

void main()
{
	// linearly interpolate between both textures (80% container, 20% awesomeface)
	vec4 texColor = mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);
	vec3 fogColor = mix(texColor.rgb, fogColor.rgb, fogDensity);
	FragColor = vec4(fogColor, texColor.a);
}