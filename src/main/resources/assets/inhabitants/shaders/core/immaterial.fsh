#version 150

layout(std140) uniform SamplerInfo {
    vec2 DiffuseSize;
};

layout(std140) uniform Time {
    float time;
};

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

const float TWO_PI = 6.28318530718;

void main() {
    float waveSpeed = 0.5;
    float waveStrength = 0.0015; 
    float waveDensity = 3.0;
    
    // wave
    float t = time * TWO_PI * waveSpeed;
    float drift = sin(texCoord.y * (2.0 * waveDensity) + t) * waveStrength;
    float interference = sin((texCoord.x + texCoord.y) * (4.0 * waveDensity) + t) * (waveStrength * 0.6);
    float shimmer = cos(texCoord.x * (6.0 * waveDensity) - t) * (waveStrength * 0.4);
    
    vec2 offset = vec2(drift + interference, interference + shimmer);
    vec2 distortedCoord = texCoord + offset;
    
    // shimmer
    float shimmerIntensity = 0.0012;
    float shimmerScale = 60.0;
    distortedCoord.x += sin(distortedCoord.y * shimmerScale + t) * shimmerIntensity;
    distortedCoord.y += cos(distortedCoord.x * shimmerScale - t) * shimmerIntensity;

    // blur
    float blurRadius = 3.0; 
    vec2 texelSize = 1.0 / DiffuseSize;
    vec4 finalColor = texture(DiffuseSampler, distortedCoord) * 0.4;
    finalColor += texture(DiffuseSampler, distortedCoord + vec2(texelSize.x * blurRadius, 0.0)) * 0.15;
    finalColor += texture(DiffuseSampler, distortedCoord - vec2(texelSize.x * blurRadius, 0.0)) * 0.15;
    finalColor += texture(DiffuseSampler, distortedCoord + vec2(0.0, texelSize.y * blurRadius)) * 0.15;
    finalColor += texture(DiffuseSampler, distortedCoord - vec2(0.0, texelSize.y * blurRadius)) * 0.15;
    
    // black effect
    float lum = dot(finalColor.rgb, vec3(0.299, 0.587, 0.114));
    
    // pulsing heartbeat
    float pulse = sin(time * TWO_PI * 1.2) * 0.5 + 0.5;
    pulse = pow(pulse, 4.0); 
    
    // desaturate
    finalColor.rgb = mix(finalColor.rgb, vec3(lum), 0.75);
    
    vec3 deep = vec3(0.05, 0.01, 0.12); 
    vec3 harsh = vec3(0.35, 0.12, 0.30);  
    vec3 purpleGrade = mix(deep, harsh, lum);
    
    // vignette
    vec2 tc = texCoord - vec2(0.5);
    float vignetteDist = length(tc);
    float vignette = vignetteDist * (1.3 + pulse * 0.6); 
    vignette = pow(vignette, 3.5);
    
    float tintIntensity = mix(0.5, 0.25, lum); 
    finalColor.rgb = mix(finalColor.rgb, purpleGrade, tintIntensity * (0.85 + vignette));
    
    // contrast boost
    finalColor.rgb = smoothstep(-0.02, 1.02, finalColor.rgb);
    
    fragColor = vec4(finalColor.rgb, 1.0);
}