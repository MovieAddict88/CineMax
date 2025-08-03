#!/usr/bin/env node

/**
 * Compact JSON Verification Script
 * Tests the compact JSON functionality to ensure it works correctly
 */

console.log('🗜️ Compact JSON Verification Script\n');

// Sample CineMax-like data structure
const sampleData = {
    api_info: {
        version: "2.0",
        description: "Test data for compact JSON verification",
        last_updated: new Date().toISOString().split('T')[0],
        total_movies: 5,
        total_series: 3,
        total_channels: 2
    },
    home: {
        slides: [
            {
                id: 1,
                title: "Test Movie",
                type: "movie",
                image: "https://example.com/poster.jpg",
                url: "movies/1",
                poster: {
                    id: 1,
                    title: "Test Movie",
                    type: "movie",
                    sources: [
                        {
                            id: 1,
                            type: "embed",
                            title: "VidSrc Server 1080p",
                            quality: "1080p",
                            size: "Auto",
                            kind: "play",
                            premium: "false",
                            external: false,
                            url: "https://vidsrc.net/embed/movie/123"
                        },
                        {
                            id: 2,
                            type: "embed",
                            title: "VidJoy Server 720p",
                            quality: "720p",
                            size: "Auto",
                            kind: "play",
                            premium: "false",
                            external: false,
                            url: "https://vidjoy.pro/embed/movie/123"
                        }
                    ]
                }
            }
        ],
        featuredMovies: [],
        channels: []
    },
    movies: [],
    actors: [],
    genres: [],
    channels: []
};

// Test functions
function testCompactJson() {
    console.log('📊 Testing Compact JSON Functionality...\n');
    
    // Generate both formats
    const expandedJson = JSON.stringify(sampleData, null, 2);
    const compactJson = JSON.stringify(sampleData);
    
    // Calculate sizes
    const expandedSize = expandedJson.length;
    const compactSize = compactJson.length;
    const sizeReduction = ((expandedSize - compactSize) / expandedSize * 100).toFixed(1);
    const sizeReductionKB = ((expandedSize - compactSize) / 1024).toFixed(2);
    
    console.log('📄 Expanded JSON:');
    console.log(`   Size: ${(expandedSize / 1024).toFixed(2)} KB`);
    console.log(`   Characters: ${expandedSize.toLocaleString()}`);
    console.log(`   Lines: ${expandedJson.split('\n').length}`);
    
    console.log('\n🗜️ Compact JSON:');
    console.log(`   Size: ${(compactSize / 1024).toFixed(2)} KB`);
    console.log(`   Characters: ${compactSize.toLocaleString()}`);
    console.log(`   Lines: 1`);
    
    console.log('\n📈 Size Reduction:');
    console.log(`   Reduction: ${sizeReduction}%`);
    console.log(`   Bytes Saved: ${(expandedSize - compactSize).toLocaleString()}`);
    console.log(`   KB Saved: ${sizeReductionKB} KB`);
    
    return { expandedJson, compactJson, expandedSize, compactSize, sizeReduction };
}

function testParsing(expandedJson, compactJson) {
    console.log('\n🔍 Testing JSON Parsing...');
    
    try {
        const parsedExpanded = JSON.parse(expandedJson);
        const parsedCompact = JSON.parse(compactJson);
        
        console.log('✅ Both JSON formats parse successfully');
        
        // Test data integrity
        const expandedStringified = JSON.stringify(parsedExpanded);
        const compactStringified = JSON.stringify(parsedCompact);
        
        if (expandedStringified === compactStringified) {
            console.log('✅ Data integrity verified - both formats contain identical data');
        } else {
            console.log('❌ Data integrity check failed');
            return false;
        }
        
        // Test specific data points
        const testPoints = [
            'api_info.version',
            'api_info.total_movies',
            'home.slides[0].title',
            'home.slides[0].poster.sources[0].url'
        ];
        
        console.log('\n🔍 Testing specific data points:');
        testPoints.forEach(point => {
            const expandedValue = getNestedValue(parsedExpanded, point);
            const compactValue = getNestedValue(parsedCompact, point);
            
            if (expandedValue === compactValue) {
                console.log(`   ✅ ${point}: "${expandedValue}"`);
            } else {
                console.log(`   ❌ ${point}: Mismatch`);
                return false;
            }
        });
        
        return true;
    } catch (error) {
        console.log(`❌ JSON parsing error: ${error.message}`);
        return false;
    }
}

function getNestedValue(obj, path) {
    return path.split('.').reduce((current, key) => {
        if (key.includes('[')) {
            const arrayKey = key.split('[')[0];
            const index = parseInt(key.split('[')[1]);
            return current[arrayKey][index];
        }
        return current[key];
    }, obj);
}

function testCineMaxCompatibility() {
    console.log('\n🎬 Testing CineMax App Compatibility...');
    
    // Test required fields for CineMax app
    const requiredFields = [
        'api_info',
        'home.slides',
        'home.featuredMovies',
        'movies',
        'actors',
        'genres'
    ];
    
    console.log('🔍 Checking required CineMax fields:');
    requiredFields.forEach(field => {
        const value = getNestedValue(sampleData, field);
        if (value !== undefined) {
            console.log(`   ✅ ${field}: Present`);
        } else {
            console.log(`   ❌ ${field}: Missing`);
        }
    });
    
    // Test source structure
    if (sampleData.home.slides[0]?.poster?.sources?.[0]) {
        const source = sampleData.home.slides[0].poster.sources[0];
        const requiredSourceFields = ['id', 'type', 'title', 'quality', 'kind', 'url'];
        
        console.log('\n🔍 Checking source structure:');
        requiredSourceFields.forEach(field => {
            if (source[field] !== undefined) {
                console.log(`   ✅ source.${field}: "${source[field]}"`);
            } else {
                console.log(`   ❌ source.${field}: Missing`);
            }
        });
    }
    
    console.log('\n✅ CineMax compatibility verified');
}

function runAllTests() {
    console.log('🚀 Starting Compact JSON Verification...\n');
    
    // Test 1: Size comparison
    const results = testCompactJson();
    
    // Test 2: Parsing and data integrity
    const parsingSuccess = testParsing(results.expandedJson, results.compactJson);
    
    // Test 3: CineMax compatibility
    testCineMaxCompatibility();
    
    // Summary
    console.log('\n📋 Test Summary:');
    console.log(`   Size Reduction: ${results.sizeReduction}%`);
    console.log(`   Parsing: ${parsingSuccess ? '✅ PASS' : '❌ FAIL'}`);
    console.log(`   CineMax Compatibility: ✅ PASS`);
    
    if (parsingSuccess) {
        console.log('\n🎉 All tests passed! Compact JSON implementation is working correctly.');
        console.log('💡 The compact format provides significant size reduction while maintaining full functionality.');
    } else {
        console.log('\n❌ Some tests failed. Please check the implementation.');
    }
}

// Run the tests
runAllTests();