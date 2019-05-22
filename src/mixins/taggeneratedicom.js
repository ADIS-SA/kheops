export default {
	tagPhotographicImage: {
		necessaryTag: [
			'00100010',
			'00100020',
			'00100030',
			'00100040',
			'0020000D',
			'00080020',
			'00080030',
			'00080090',
			'00200010',
			'00080050',
			'00080005',
			'00200010'
		],
		createTag: [
			{ tag: '00080060', 'vr': 'CS', value: ['XC'] },
			{ tag: '0020000E', 'vr': 'UI', value: 'OID' },
			{ tag: '00200011', 'vr': 'IS', value: '' },
			{ tag: '00080070', 'vr': 'LO', value: '' },
			{ tag: '7FE00010', 'vr': 'OB', value: 'BulkDataURI' },
			{ tag: '00080016', 'vr': 'UI', value: ['1.2.840.10008.5.1.4.1.1.77.1.4'] },
			{ tag: '00080018', 'vr': 'UI', value: 'OID' },
			{ tag: '00200013', 'vr': 'IS', value: [1] },
			{ tag: '00280002', 'vr': 'US', value: '' },
			{ tag: '00280004', 'vr': 'CS', value: '' },
			{ tag: '00280010', 'vr': 'US', value: '' },
			{ tag: '00280011', 'vr': 'US', value: '' },
			{ tag: '00280100', 'vr': 'US', value: '' },
			{ tag: '00280101', 'vr': 'US', value: '' },
			{ tag: '00280102', 'vr': 'US', value: '' },
			{ tag: '00280103', 'vr': 'US', value: '' },
			{ tag: '00282110', 'vr': 'CS', value: ['01'] },
			{ tag: '00400555', 'vr': 'SQ', value: '' },
			{ tag: '00080008', 'vr': 'CS', value: ['ORIGINAL', 'PRIMARY'] }
		],
		tagBulkDataURI: '7FE00010'
	},
	tagEncapsulatedPDF: {
		necessaryTag: [
			'00100010',
			'00100020',
			'00100030',
			'00100040',
			'0020000D',
			'00080020',
			'00080030',
			'00080090',
			'00200010',
			'00080050',
			'00080005',
			'00200010'
		],
		createTag: [
			{ tag: '00080060', 'vr': 'CS', value: ['DOC'] },
			{ tag: '0020000E', 'vr': 'UI', value: 'OID' },
			{ tag: '00200011', 'vr': 'IS', value: '' },
			{ tag: '00080070', 'vr': 'LO', value: '' },
			{ tag: '00080064', 'vr': 'CS', value: ['SD'] },
			{ tag: '00200013', 'vr': 'IS', value: [1] },
			{ tag: '00080023', 'vr': 'DA', value: '' },
			{ tag: '00080033', 'vr': 'TM', value: '' },
			{ tag: '0008002A', 'vr': 'DT', value: '' },
			{ tag: '00280301', 'vr': 'CS', value: ['YES'] },
			{ tag: '00420010', 'vr': 'ST', value: '' },
			{ tag: '0040A043', 'vr': 'SQ', value: '' },
			{ tag: '00420012', 'vr': 'LO', value: ['application/pdf'] },
			{ tag: '00420011', 'vr': 'OB', value: 'BulkDataURI' },
			{ tag: '00080016', 'vr': 'UI', value: ['1.2.840.10008.5.1.4.1.1.104.1'] },
			{ tag: '00080018', 'vr': 'UI', value: 'OID' }
		],
		tagBulkDataURI: '00420011'
	}
}
